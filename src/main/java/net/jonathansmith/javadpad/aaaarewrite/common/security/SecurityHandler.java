/*
 * Copyright (C) 2013 Jon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jonathansmith.javadpad.aaaarewrite.common.security;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;

/**
 *
 * @author Jon
 */
public class SecurityHandler {
    
    public static final boolean DECRYPT_MODE = false;
    public static final boolean ENCRYPT_MODE = true;
    
    private static final ConcurrentHashMap<String, AsymmetricCipherKeyPair> serverKeys = new ConcurrentHashMap<String, AsymmetricCipherKeyPair> ();
    private static final Provider provider;
    private static final SecurityHandler instance;
    
    private byte[] sharedKey;
    
    public static SecurityHandler getInstance() {
        return instance;
    }
    
    public AsymmetricCipherKeyPair getKeyPair() {
        AsymmetricCipherKeyPair pair = serverKeys.get("RSA");
        if (pair != null) {
            return pair;
        }
        
        if (provider == null) {
            return pair;
        }
        
        SecureRandom secureRandom = this.getSecureRandom("SHA1PRNG", "SUN");
        RSAKeyGenerationParameters params = new RSAKeyGenerationParameters(new BigInteger("15201", 16), secureRandom, 1024, 80);
        AsymmetricCipherKeyPairGenerator generator = new RSAKeyPairGenerator();
        generator.init(params);
        
        AsymmetricCipherKeyPair newPair = generator.generateKeyPair();
        AsymmetricCipherKeyPair oldPair = serverKeys.putIfAbsent("RSA", newPair);
        if (oldPair != null) {
            return oldPair;
        }
        
        return newPair;
    }
    
    private SecureRandom getSecureRandom(String RNGAlgorithm, String RNGProvider) {
        try {
            SecureRandom r;
            if (RNGProvider != null) {
                r = SecureRandom.getInstance(RNGAlgorithm, RNGProvider);
            }
            
            else {
                r = SecureRandom.getInstance(RNGAlgorithm);
            }
            r.nextBytes(new byte[1]);
            return r;
        }
        
        catch (NoSuchProviderException ex) {
            return getSecureRandom(RNGAlgorithm, null);
        } 
        
        catch (NoSuchAlgorithmException ex) {
            if (RNGProvider != null) {
                return getSecureRandom(RNGAlgorithm, null);
            }
            
            System.out.println("Could not find algorithm to generate rng");
            throw new RuntimeException("No such provider!");
        }
    }
    
    public byte[] encodeKey(CipherParameters key) {
        if (!(key instanceof RSAKeyParameters)) {
            return null;
        }
        
        if (((RSAKeyParameters) key).isPrivate()) {
            return null;
        }
        
        RSAKeyParameters rsaKey = (RSAKeyParameters) key;
        
        ASN1EncodableVector encodable = new ASN1EncodableVector();
        encodable.add(new ASN1Integer(rsaKey.getModulus()));
        encodable.add(new ASN1Integer(rsaKey.getExponent()));
        
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, new DERNull()), new DERSequence(encodable));
    }
    
    public byte[] getSymetricKey() {
        if (sharedKey != null) {
            return sharedKey;
        }
        
        sharedKey = new byte[16];
        final Random rand = new Random();
        rand.nextBytes(sharedKey);
        return sharedKey;
    }
    
    public AsymmetricBlockCipher getAsymmetricCipher() {
        return new PKCS1Encoding(new RSAEngine());
    }
    
    public CFBBlockCipher getSymmetricCipher() {
        return new CFBBlockCipher(new AESEngine(), 8);
    }
    
    public byte[] processAll(AsymmetricBlockCipher cipher, byte[] input) {
        int outputSize = 0;
        int blockSize = cipher.getInputBlockSize();
        List<byte[]> outputBlocks = new LinkedList<byte[]> ();
        
        int pos = 0;
        
        while (pos < input.length) {
            int length = Math.min(input.length - pos, blockSize);
            byte[] result;
            try {
                result = cipher.processBlock(input, pos, length);
            }
            
            catch (InvalidCipherTextException e) {
                System.out.println("Invalid cipher!");
                e.printStackTrace();
                return null;
            }
            
            outputSize += result.length;
            outputBlocks.add(result);
        }
            
        byte[] output = new byte[outputSize];

        pos = 0;
        for (byte[] block : outputBlocks) {
            System.arraycopy(block, 0, output, pos, block.length);
            pos += block.length;
        }

        return output;
    }
    
    static {
        Provider p = Security.getProvider("BC");
        if (p == null) {
            Security.addProvider(new BouncyCastleProvider());
            p = Security.getProvider("BC");
            if (p == null) {
                throw new RuntimeException("Could not start security handler");
            }
        }
            
        provider = p;
        instance = new SecurityHandler();
    }
    
}
