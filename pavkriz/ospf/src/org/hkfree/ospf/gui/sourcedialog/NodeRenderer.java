package org.hkfree.ospf.gui.sourcedialog;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Předefinovaný DefaultTreeCellRenderer pro použití zobrazení rozbalovacího stromu se zaškrtávacími koncovými prvky
 * @author Jakub Menzel
 */
public class NodeRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;
    protected JCheckBox checkBoxRenderer = new JCheckBox();


    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
	    boolean leaf, int row, boolean hasFocus) {
	if (value instanceof DefaultMutableTreeNode) {
	    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	    Object userObject = node.getUserObject();
	    if (userObject instanceof FileNameCheckBoxNode) {
		FileNameCheckBoxNode fileNode = (FileNameCheckBoxNode) userObject;
		prepareQuestionRenderer(fileNode, selected);
		return checkBoxRenderer;
	    }
	}
	return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }


    protected void prepareQuestionRenderer(FileNameCheckBoxNode tfq, boolean selected) {
	checkBoxRenderer.setText(tfq.getName());
	checkBoxRenderer.setSelected(tfq.isSelected());
	if (selected) {
	    checkBoxRenderer.setForeground(getTextSelectionColor());
	    checkBoxRenderer.setBackground(getBackgroundSelectionColor());
	} else {
	    checkBoxRenderer.setForeground(getTextNonSelectionColor());
	    checkBoxRenderer.setBackground(getBackgroundNonSelectionColor());
	}
    }
}