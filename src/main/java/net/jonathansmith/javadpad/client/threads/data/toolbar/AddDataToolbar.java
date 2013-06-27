/*
 * Copyright (C) 2013 jonathansmith
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
package net.jonathansmith.javadpad.client.threads.data.toolbar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.layout.GroupLayout;

/**
 *
 * @author jonathansmith
 */
public class AddDataToolbar extends JPanel {

    public AddDataToolbar() {
        initComponents();
    }

    public void addDisplayOptionListener(ActionListener listener) {
        this.setPlugin.addActionListener(listener);
        this.addFiles.addActionListener(listener);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new JLabel();
        setPlugin = new JButton();
        addFiles = new JButton();
        run = new JButton();
        submit = new JButton();
        back = new JButton();

        setMaximumSize(new Dimension(120, 32767));
        setMinimumSize(new Dimension(120, 0));

        jLabel1.setText("Data Toolbar:");

        setPlugin.setText("Set Plugin");

        addFiles.setText("Add FIles");

        run.setText("Run");

        submit.setText("Submit");

        back.setText("Back");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(layout.createParallelGroup(GroupLayout.CENTER)
                            .add(submit, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                            .add(run, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(addFiles, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .add(setPlugin, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .add(back, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)))
                    .add(jLabel1))
                .add(5, 5, 5))
        );

        layout.linkSize(new Component[] {addFiles, back, run, setPlugin, submit}, GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .add(5, 5, 5)
                .add(setPlugin)
                .add(5, 5, 5)
                .add(addFiles)
                .add(5, 5, 5)
                .add(run)
                .add(5, 5, 5)
                .add(submit)
                .add(5, 5, 5)
                .add(back)
                .add(0, 116, Short.MAX_VALUE))
        );

        layout.linkSize(new Component[] {addFiles, back, run, setPlugin, submit}, GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public JButton addFiles;
    public JButton back;
    private JLabel jLabel1;
    public JButton run;
    public JButton setPlugin;
    public JButton submit;
    // End of variables declaration//GEN-END:variables
}