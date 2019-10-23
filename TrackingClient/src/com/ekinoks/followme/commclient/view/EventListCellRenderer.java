package com.ekinoks.followme.commclient.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.ekinoks.followme.trackingutils.events.EventType;
import com.ekinoks.followme.trackingutils.events.EventWrapper;
import com.ekinoks.ui.icon.IconDefaults;
import com.ekinoks.ui.icon.IconStore;

import sun.swing.DefaultLookup;

@SuppressWarnings({ "serial", "restriction" })
public class EventListCellRenderer extends EventComponent implements ListCellRenderer<EventWrapper> {

	private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
	private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
	protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;

	public EventListCellRenderer() {

		setOpaque(true);
		setBorder(getNoFocusBorder());

	}

	private Border getNoFocusBorder() {

		Border border = DefaultLookup.getBorder(this, ui, "List.cellNoFocusBorder");

		if (System.getSecurityManager() != null) {

			if (border != null)

				return border;

			return SAFE_NO_FOCUS_BORDER;

		} else {

			if (border != null && (noFocusBorder == null || noFocusBorder == DEFAULT_NO_FOCUS_BORDER)) {
				return border;

			}

			return noFocusBorder;
		}
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends EventWrapper> list, EventWrapper event, int index,
			boolean isSelected, boolean cellHasFocus) {

		setComponentOrientation(list.getComponentOrientation());

		Color bg = null;
		Color fg = null;

		if (isSelected) {

			setBackground(bg == null ? Color.RED : bg);
			setForeground(fg == null ? Color.RED : fg);
			list.repaint();

		} else {

			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		if (EventType.SeatBeltEvent.equals(event.getEventType())) {

			setIcon(IconStore.getStore().getIcon(IconDefaults.import_));

		} else if (EventType.LocationEvent.equals(event.getEventType())) {

			setIcon(IconStore.getStore().getIcon(IconDefaults.arrow_down));

		} else if (EventType.EngineEvent.equals(event.getEventType())) {

			setIcon(IconStore.getStore().getIcon(IconDefaults.cevrim));

		}

		setLblName(event.getDeviceID());
		setLblEventTime(event.getTimeStamp());
		setLblEventType(event.getEventType().name());
		setEnabled(list.isEnabled());
		setFont(list.getFont());

		Border border = null;

		if (cellHasFocus) {

			if (isSelected) {

				border = DefaultLookup.getBorder(this, ui, "List.focusSelectedCellHighlightBorder");
			}

			if (border == null) {

				border = DefaultLookup.getBorder(this, ui, "List.focusCellHighlightBorder");
			}

		} else {

			border = getNoFocusBorder();
		}

		setBorder(border);

		return this;
	}

	public static class UIResource extends DefaultListCellRenderer implements javax.swing.plaf.UIResource {
	}
}
