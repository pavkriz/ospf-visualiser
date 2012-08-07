package org.hkfree.ospf.gui.sourcedialog;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Předefinovaný DefaultCellEditor pro použití zobrazení rozbalovacího stromu se zaškrtávacími koncovými prvky
 * @author Jakub Menzel
 */
public class NodeEditor extends DefaultCellEditor {

    private static final long serialVersionUID = 1L;
    protected FileNameCheckBoxNode nodeData;


    /**
     * Konstruktor
     */
    public NodeEditor() {
	super(new JCheckBox());
    }


    /**
     * přepsaná metoda
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
	    int row) {
	JCheckBox editor = null;
	nodeData = getNodeTadaValue(value);
	if (nodeData != null) {
	    editor = (JCheckBox) (super.getComponent());
	    editor.setText(nodeData.getName());
	    editor.setSelected(nodeData.isSelected());
	}
	return editor;
    }


    public static FileNameCheckBoxNode getNodeTadaValue(Object value) {
	if (value instanceof DefaultMutableTreeNode) {
	    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	    Object userObject = node.getUserObject();
	    if (userObject instanceof FileNameCheckBoxNode) {
		return (FileNameCheckBoxNode) userObject;
	    }
	}
	return null;
    }


    public Object getCellEditorValue() {
	JCheckBox editor = (JCheckBox) (super.getComponent());
	nodeData.setSelected(editor.isSelected());
	return nodeData;
    }
}