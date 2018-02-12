/*-
 * #%L
 * Fiji plugin for the annotation of massive, multi-view data.
 * %%
 * Copyright (C) 2012 - 2016 MaMuT development team.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package fiji.plugin.mamut.gui;

import fiji.plugin.trackmate.Logger;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.SelectionModel;
import fiji.plugin.trackmate.TrackModel;
import fiji.plugin.trackmate.gui.panels.ActionListenablePanel;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.GridLayout;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class ProofreadingPanel extends ActionListenablePanel {

	/*
	 * FIELDS
	 */

	private static final long serialVersionUID = 1L;

	protected Logger logger = Logger.IJ_LOGGER;

	private JLabel lblTrack;
	private final String labelNoTracks = "No Tracks";

	/**
	 * Create the panel.
	 */
	public ProofreadingPanel( final SelectionModel selectionModel, final Model model ) {
		setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Track Inspector", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_panel.rowWeights = new double[] { 0.0 };
		panel.setLayout(gbl_panel);

		JButton buttonFirst = new JButton("<<");
		buttonFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lblTrack.getText().equals(labelNoTracks) || model.getTrackModel().nTracks(false) == 0) {
					return;
				}
				final TrackModel trackModel = model.getTrackModel();
				int firstTrackID = trackModel.trackIDs(false).iterator().next();
				lblTrack.setText(trackModel.name(firstTrackID));
				selectionModel.clearSelection();
				selectionModel.selectTrack(trackModel.trackSpots(firstTrackID), trackModel.trackEdges(firstTrackID), 0);
			}
		});
		GridBagConstraints gbc_buttonFirst = new GridBagConstraints();
		gbc_buttonFirst.weightx = 1.0;
		gbc_buttonFirst.weighty = 1.0;
		gbc_buttonFirst.insets = new Insets(0, 0, 0, 5);
		gbc_buttonFirst.gridx = 0;
		gbc_buttonFirst.gridy = 0;
		panel.add(buttonFirst, gbc_buttonFirst);
		
		JButton buttonPrev = new JButton("<");
		buttonPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lblTrack.getText().equals(labelNoTracks) || model.getTrackModel().nTracks(false) == 0) {
					return;
				}
				String currentTrackName = lblTrack.getText();
				int previousTrackID = -1;
				final TrackModel trackModel = model.getTrackModel();
				Iterator<Integer> it = trackModel.trackIDs(false).iterator();
				while (it.hasNext()) {
					int trackID = it.next();
					if (trackModel.name(trackID).equals(currentTrackName)) break;
					previousTrackID = trackID;
				}
				if (previousTrackID == -1) {
					return;
				}
				lblTrack.setText(trackModel.name(previousTrackID));
				selectionModel.clearSelection();
				selectionModel.selectTrack(trackModel.trackSpots(previousTrackID), trackModel.trackEdges(previousTrackID), 0);
			}
		});
		GridBagConstraints gbc_buttonPrev = new GridBagConstraints();
		gbc_buttonPrev.weighty = 1.0;
		gbc_buttonPrev.weightx = 1.0;
		gbc_buttonPrev.insets = new Insets(0, 0, 0, 5);
		gbc_buttonPrev.gridx = 1;
		gbc_buttonPrev.gridy = 0;
		panel.add(buttonPrev, gbc_buttonPrev);

		lblTrack = new JLabel();
		lblTrack.setHorizontalAlignment(SwingConstants.CENTER);
		TrackModel trackModel = model.getTrackModel();
		int trackID = 0 < trackModel.nTracks(false) ? trackModel.trackIDs(false).iterator().next() : -1;
		lblTrack.setText(trackID != -1 ? trackModel.name(trackID) : labelNoTracks);
		GridBagConstraints gbc_lblTrack = new GridBagConstraints();
		gbc_lblTrack.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTrack.weighty = 1.0;
		gbc_lblTrack.weightx = 1.0;
		gbc_lblTrack.insets = new Insets(0, 0, 0, 5);
		gbc_lblTrack.gridx = 2;
		gbc_lblTrack.gridy = 0;
		panel.add(lblTrack, gbc_lblTrack);

		JButton buttonNext = new JButton(">");
		buttonNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lblTrack.getText().equals(labelNoTracks) || model.getTrackModel().nTracks(false) == 0) {
					return;
				}
				String currentTrackName = lblTrack.getText();
				final TrackModel trackModel = model.getTrackModel();
				Iterator<Integer> it = trackModel.trackIDs(false).iterator();
				while (it.hasNext()) {
					if (trackModel.name(it.next()).equals(currentTrackName)) break;					
				}
				if (!it.hasNext()) {
					return;
				}
				int nextTrackID = it.next();
				lblTrack.setText(trackModel.name(nextTrackID));
				selectionModel.clearSelection();
				selectionModel.selectTrack(trackModel.trackSpots(nextTrackID), trackModel.trackEdges(nextTrackID), 0);
			}
		});
		GridBagConstraints gbc_buttonNext = new GridBagConstraints();
		gbc_buttonNext.weighty = 1.0;
		gbc_buttonNext.weightx = 1.0;
		gbc_buttonNext.insets = new Insets(0, 0, 0, 5);
		gbc_buttonNext.gridx = 3;
		gbc_buttonNext.gridy = 0;
		panel.add(buttonNext, gbc_buttonNext);
		
		JButton buttonLast = new JButton(">>");
		buttonLast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final TrackModel trackModel = model.getTrackModel();
				if (lblTrack.getText().equals(labelNoTracks) || trackModel.nTracks(false) == 0) {
					return;
				}
				Iterator<Integer> it = trackModel.trackIDs(false).iterator();
				int lastTrackID = it.next();
				while (it.hasNext()) {
					lastTrackID = it.next();					
				}
				lblTrack.setText(trackModel.name(lastTrackID));
				selectionModel.clearSelection();
				selectionModel.selectTrack(trackModel.trackSpots(lastTrackID), trackModel.trackEdges(lastTrackID), 0);
			}
		});
		GridBagConstraints gbc_buttonLast = new GridBagConstraints();
		gbc_buttonLast.weightx = 1.0;
		gbc_buttonLast.weighty = 1.0;
		gbc_buttonLast.insets = new Insets(0, 0, 0, 5);
		gbc_buttonLast.gridx = 4;
		gbc_buttonLast.gridy = 0;
		panel.add(buttonLast, gbc_buttonLast);
	}

}
