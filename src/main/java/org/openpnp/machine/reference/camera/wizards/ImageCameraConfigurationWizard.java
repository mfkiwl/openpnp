/*
 * Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
 * 
 * This file is part of OpenPnP.
 * 
 * OpenPnP is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * OpenPnP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with OpenPnP. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * For more information about OpenPnP visit http://openpnp.org
 */

package org.openpnp.machine.reference.camera.wizards;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.openpnp.gui.components.ComponentDecorators;
import org.openpnp.gui.support.AbstractConfigurationWizard;
import org.openpnp.gui.support.IntegerConverter;
import org.openpnp.machine.reference.camera.ImageCamera;
import org.openpnp.util.UiUtils;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class ImageCameraConfigurationWizard extends AbstractConfigurationWizard {
    private final ImageCamera camera;

    private JPanel panelGeneral;
    private JLabel lblSourceUrl;
    private JTextField textFieldSourceUrl;
    private JButton btnBrowse;

    public ImageCameraConfigurationWizard(ImageCamera camera) {
        this.camera = camera;

        panelGeneral = new JPanel();
        contentPanel.add(panelGeneral);
        panelGeneral.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null),
                "General", TitledBorder.LEADING, TitledBorder.TOP, null));
        panelGeneral.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("max(50dlu;default):grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,}));
        
        lblWidth = new JLabel("Width");
        panelGeneral.add(lblWidth, "2, 2, right, default");
        
        width = new JTextField();
        panelGeneral.add(width, "4, 2, fill, default");
        width.setColumns(10);
        
        lblHeight = new JLabel("Height");
        panelGeneral.add(lblHeight, "2, 4, right, default");
        
        height = new JTextField();
        panelGeneral.add(height, "4, 4, fill, default");
        height.setColumns(10);

        lblSourceUrl = new JLabel("Source URL");
        panelGeneral.add(lblSourceUrl, "2, 8, right, default");

        textFieldSourceUrl = new JTextField();
        panelGeneral.add(textFieldSourceUrl, "4, 8, 3, 1, fill, default");
        textFieldSourceUrl.setColumns(40);

        btnBrowse = new JButton(browseAction);
        panelGeneral.add(btnBrowse, "8, 8");
    }

    @Override
    public void createBindings() {
        IntegerConverter intConverter = new IntegerConverter();

        addWrappedBinding(camera, "width", width, "text", intConverter);
        addWrappedBinding(camera, "height", height, "text", intConverter);

        addWrappedBinding(camera, "sourceUri", textFieldSourceUrl, "text");

        ComponentDecorators.decorateWithAutoSelect(width);
        ComponentDecorators.decorateWithAutoSelect(height);
        ComponentDecorators.decorateWithAutoSelect(textFieldSourceUrl);
    }

    private Action browseAction = new AbstractAction() {
        {
            putValue(NAME, "Browse");
            putValue(SHORT_DESCRIPTION, "Browse");
        }

        public void actionPerformed(ActionEvent e) {
            FileDialog fileDialog = new FileDialog((Frame) getTopLevelAncestor());
            fileDialog.setFilenameFilter(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String[] extensions = new String[] {".png", ".jpg", ".gif", ".tif", ".tiff"};
                    for (String extension : extensions) {
                        if (name.toLowerCase().endsWith(extension)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
            fileDialog.setVisible(true);
            if (fileDialog.getFile() == null) {
                return;
            }
            File file = new File(new File(fileDialog.getDirectory()), fileDialog.getFile());
            textFieldSourceUrl.setText(file.toURI().toString());
        }
    };
    private JLabel lblWidth;
    private JLabel lblHeight;
    private JTextField width;
    private JTextField height;

    @Override
    protected void saveToModel() {
        super.saveToModel();
        UiUtils.messageBoxOnException(() -> {
            camera.reinitialize(); 
        });
    }
}
