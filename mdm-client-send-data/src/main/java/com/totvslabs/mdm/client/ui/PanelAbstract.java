package com.totvslabs.mdm.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.totvslabs.mdm.client.pojoTSA.MasterConfigurationData;

public abstract class PanelAbstract extends JPanel {
	private static final long serialVersionUID = 1L;

	protected CellConstraints cc;
	private FormLayout layoutGeneral;
	private Integer actualRow = 2;
	protected final List<JComponent> fieldsToNotEnable = new ArrayList<JComponent>();
	protected final List<JComponent> fieldsToNotDisable = new ArrayList<JComponent>();

	public PanelAbstract(int colNumber, int rowNumber, String borderName){
		this.setCc(new CellConstraints());
		this.setLayoutGeneral(new FormLayout( "30px, " + this.generateStringLayout(colNumber, 300) + ", 30px", "10px, " + this.generateStringLayout(rowNumber, 21) + ", 10px" ));
		
		this.setLayout(this.getLayoutGeneral());
//		this.setBackground(Color.GREEN);

		this.setBorder( BorderFactory.createTitledBorder(borderName) );
	}

	public static MaskFormatter getMaskFormatter(String mask) {
		MaskFormatter maskFormatter = null;

		try {
			maskFormatter = new MaskFormatter(mask);
			maskFormatter.setPlaceholderCharacter('_');
		} catch (ParseException e) {
		}

		return maskFormatter;
	}

	private String generateStringLayout(int number, int size) {
		StringBuilder sb = new StringBuilder();

		for(int i=0; i<number; i++) {
			sb.append(size);

			if(i<number+1) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	protected void add(JComponent[] components, int rowSpan) {
		if(components != null) {
			int colNumber = 2;

			for (JComponent component : components) {
				this.add(component, colNumber, false, rowSpan);

				colNumber++;
			}

			this.actualRow = rowSpan + this.actualRow;
		}
	}

	protected void add(JComponent[] components) {
		this.add(components, 1);
	}

	public abstract void initializeLayout();
	public abstract void fillComponents(MasterConfigurationData masterConfigurationData);
	public abstract void fillData(MasterConfigurationData masterConfigurationData);

	public void disableFields() {
		disableFields(this);
	}

	public void enableDisableField(JComponent component, boolean enable) {
		component.setEnabled(enable);

		if(enable) {
			this.fieldsToNotEnable.remove(component);
		}
		else {
			this.fieldsToNotEnable.add(component);
		}
	}

	public void disableFields(JPanel panel) {
		Component[] components = panel.getComponents();

		if(components != null) {
			for (Component component : components) {
				if(component instanceof JPanel) {
					disableFields((JPanel) component);
				}
				else {
					processEnableDisableComponent(component, false);
				}
			}
		}
	}

	public void enableFields() {
		enableFields(this);
	}

	private void enableFields(JPanel panel) {
		Component[] components = panel.getComponents();

		if(components != null) {
			for (Component component : components) {
				if(component instanceof JPanel) {
					enableFields((JPanel) component);
				}
				else {
					processEnableDisableComponent(component, true);
				}
			}
		}
	}

	private void processEnableDisableComponent(Component component, boolean enable) {
		if((!enable && !fieldsToNotDisable.contains(component)) || (enable && !fieldsToNotEnable.contains(component))) {
			component.setEnabled(enable);
		}
	}

	protected void add(JComponent component) {
		this.add(component, 2);
	}

	protected void add(JComponent component, int colNumber) {
		this.add(component, colNumber, true);
	}

	protected void add(JComponent component, int colNumber, boolean addRow) {
		this.add(component, colNumber, addRow, 1);
	}

	protected void add(final JComponent component, int colNumber, boolean addRow, int rowSpan, int colSpan) {
		this.add(component, this.cc.xywh(colNumber, this.actualRow, colSpan, rowSpan));

		if(addRow) {
			this.actualRow = this.actualRow + rowSpan;
		}
	}

	protected void add(final JComponent component, int colNumber, boolean addRow, int rowSpan) {
		this.add(component, colNumber, addRow, rowSpan, 1);
	}

	public CellConstraints getCc() {
		return cc;
	}

	public void setCc(CellConstraints cc) {
		this.cc = cc;
	}

	public FormLayout getLayoutGeneral() {
		return layoutGeneral;
	}

	public void setLayoutGeneral(FormLayout layoutGeneral) {
		this.layoutGeneral = layoutGeneral;
	}
}