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
package net.jonathansmith.javadpad.client.threads.plugin.gui.toolbar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Jon
 */
public class PluginSelectToolbar extends JPanel {

    /**
     * Creates new form ClientMainToolbar
     */
    public PluginSelectToolbar() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        findLoaders = new JButton();
        findAnalysers = new JButton();
        back = new JButton();

        setMaximumSize(new Dimension(120, 32767));
        setMinimumSize(new Dimension(120, 0));
        setPreferredSize(new Dimension(120, 300));

        findLoaders.setText("Select Loader");
        findLoaders.setToolTipText("Choose a loading plugin to upload");
        findLoaders.setBorder(null);
        findLoaders.setMargin(new Insets(2, 8, 2, 8));
        findLoaders.setMaximumSize(new Dimension(105, 23));
        findLoaders.setMinimumSize(new Dimension(105, 23));
        findLoaders.setPreferredSize(new Dimension(105, 23));

        findAnalysers.setText("Select Analyser");
        findAnalysers.setToolTipText("Choose an anlyser plugin to upload");
        findAnalysers.setBorder(null);
        findAnalysers.setMargin(new Insets(2, 8, 2, 8));
        findAnalysers.setMaximumSize(new Dimension(105, 23));
        findAnalysers.setMinimumSize(new Dimension(105, 23));
        findAnalysers.setPreferredSize(new Dimension(105, 23));

        back.setText("Back");
        back.setToolTipText("Return to previous screen");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(findLoaders, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                    .addComponent(findAnalysers, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                    .addComponent(back, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {back, findAnalysers, findLoaders});

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(findLoaders, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(findAnalysers, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(back)
                .addContainerGap(221, Short.MAX_VALUE))
        );

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {back, findAnalysers, findLoaders});

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public JButton back;
    public JButton findAnalysers;
    public JButton findLoaders;
    // End of variables declaration//GEN-END:variables
}