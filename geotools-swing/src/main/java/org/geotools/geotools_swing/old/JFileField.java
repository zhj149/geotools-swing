/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.geotools_swing.old;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.Parameter;
import org.geotools.geotools_swing.old.JWizard.Controller;

import net.miginfocom.swing.MigLayout;

/**
 * Widget for File; provides a "Browse" button to open a file dialog.
 * 
 * @author Jody Garnett
 *
 *
 *
 *
 * @source $URL$
 */
public class JFileField extends ParamField {

    private JTextField field;

    private JButton browse;

    public JFileField(Parameter<?> parameter) {
        super(parameter);
    }

    public JComponent doLayout() {
        final JPanel panel = new JPanel(new MigLayout("insets 0"));
        panel.add(field = new JTextField(32), "width 200::700, growx");
        // field.setActionCommand(FINISH); // pressing return will Finish wizard

        panel.add(browse = new JButton("Browse"), "wrap");
        browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browse();
            }
        });
        return panel;
    }

    @SuppressWarnings("unchecked")
    protected void browse() {
        JFileChooser dialog;
        Object format = this.parameter.metadata == null ? null : this.parameter.metadata.get( Parameter.EXT );
        if (format instanceof FileDataStoreFactorySpi) {
            dialog = new JFileDataStoreChooser((FileDataStoreFactorySpi) format);
        } else if (format instanceof String) {
            dialog = new JFileDataStoreChooser((String) format);
        } else if (format instanceof String[]) {
            dialog = new JFileDataStoreChooser((String[]) format);
        }
        else if (format instanceof List) {
            dialog = new JFileDataStoreChooser((List<String>) format);
        }
        else {
            dialog = new JFileChooser();
        }
        dialog.setSelectedFile( getValue() );
        
        int returnVal = dialog.showOpenDialog( browse );
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = dialog.getSelectedFile();
            setValue( file );
        }
    }
    
    public void addListener(Controller controller) {
        //field.addKeyListener(controller);
        field.addActionListener(controller);
        field.getDocument().addDocumentListener(controller);
    }

    public void removeListener(Controller controller) {
        //field.removeKeyListener(controller);
        field.removeActionListener(controller);
        field.getDocument().removeDocumentListener(controller);
    }
    
    public File getValue(){
        String txt = field.getText();
        if (txt == null || txt.equals("")) {
            return null;
        }
        try {
            File file = new File(txt);
            return file;
        } catch (Exception e) {
        }
        try {
            URL url = new URL(txt);
            return DataUtilities.urlToFile( url );            
        } catch (MalformedURLException e) {
        }
        return null; // not a file
    }

    public void setValue(Object value) {
        if( value instanceof File ){
            File file = (File) value;
            field.setText( file.toString() );            
        }
        else if ( value instanceof URL ){
            URL url = (URL) value;
            field.setText( url.toExternalForm() );
        }
        else if ( value instanceof String){
            field.setText( (String) value );
        }
    }

    public boolean validate() {
        String txt = field.getText();
        if (txt == null || txt.equals("")) {
            return !parameter.required;
        }
        File file = getValue();
        if( file != null ){
            return file.exists();
        }
        return false;
    }

}
