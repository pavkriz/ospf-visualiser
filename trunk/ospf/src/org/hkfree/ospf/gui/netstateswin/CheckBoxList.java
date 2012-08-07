package org.hkfree.ospf.gui.netstateswin;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListSelectionModel;


/**
 * Třída představující list položek k zaškrtnutí
 * @author Jakub Menzel
 * @author Jan Schovánek
 */
public class CheckBoxList extends JList {

    private static final long serialVersionUID = 1L;


    /**
     * Konstruktor
     */
    public CheckBoxList() {
	super();
	setModel(new DefaultListModel());
	setCellRenderer(new CheckboxCellRenderer());
	addMouseListener(new MouseAdapter() {

	    @Override
	    public void mousePressed(MouseEvent e) {
		int index = locationToIndex(e.getPoint());
		if (index != -1) {
		    Object obj = getModel().getElementAt(index);
		    if (obj instanceof JCheckBox) {
			JCheckBox checkbox = (JCheckBox) obj;
			checkbox.setSelected(!checkbox.isSelected());
			repaint();
		    }
		}
	    }
	});
	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }


    /**
     * Vrací indexy zaškrtnutých checkboxů
     * @return pole obsahující indexy zaškrtnutých checkboxů
     */
    public int[] getCheckedIdexes() {
	List<Integer> list = new ArrayList<Integer>();
	DefaultListModel dlm = (DefaultListModel) getModel();
	for (int i = 0; i < dlm.size(); ++i) {
	    Object obj = getModel().getElementAt(i);
	    if (obj instanceof JCheckBox) {
		JCheckBox checkbox = (JCheckBox) obj;
		if (checkbox.isSelected()) {
		    list.add(new Integer(i));
		}
	    }
	}
	int[] indexes = new int[list.size()];
	for (int i = 0; i < list.size(); ++i) {
	    indexes[i] = ((Integer) list.get(i)).intValue();
	}
	return indexes;
    }


    /**
     * Vrací seznam zaškrtnutých checkboxů
     * @return zaškrtnuté checkboxy
     */
    public List<JCheckBox> getCheckedItems() {
	List<JCheckBox> list = new ArrayList<JCheckBox>();
	DefaultListModel dlm = (DefaultListModel) getModel();
	for (int i = 0; i < dlm.size(); ++i) {
	    Object obj = getModel().getElementAt(i);
	    if (obj instanceof JCheckBox) {
		JCheckBox checkbox = (JCheckBox) obj;
		if (checkbox.isSelected()) {
		    list.add(checkbox);
		}
	    }
	}
	return list;
    }


    /**
     * Nastaví vše zaškrtnuto
     */
    public void setAllChecked() {
	DefaultListModel dlm = (DefaultListModel) getModel();
	for (int i = 0; i < dlm.size(); ++i) {
	    Object obj = getModel().getElementAt(i);
	    if (obj instanceof JCheckBox) {
		((JCheckBox) obj).setSelected(true);
	    }
	}
	repaint();
    }


    /**
     * Nastaví vše odškrtnuto
     */
    public void setNoneChecked() {
	DefaultListModel dlm = (DefaultListModel) getModel();
	for (int i = 0; i < dlm.size(); ++i) {
	    Object obj = getModel().getElementAt(i);
	    if (obj instanceof JCheckBox) {
		((JCheckBox) obj).setSelected(false);
	    }
	}
	repaint();
    }
}

/**
 * Třída představující centerer listu položek k zaškrtnutí
 * @author Jakub Menzel
 */
class CheckboxCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	    boolean cellHasFocus) {
	if (value instanceof ModelCheckBoxItem) {
	    ModelCheckBoxItem checkbox = (ModelCheckBoxItem) value;
	    checkbox.setBackground(list.getBackground());
	    return checkbox;
	} else {
	    return super.getListCellRendererComponent(list, value.getClass().getName(), index, isSelected, cellHasFocus);
	}
    }
}
