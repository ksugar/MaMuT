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
import fiji.plugin.trackmate.ModelChangeEvent;
import fiji.plugin.trackmate.ModelChangeListener;
import fiji.plugin.trackmate.SelectionChangeEvent;
import fiji.plugin.trackmate.SelectionChangeListener;
import fiji.plugin.trackmate.SelectionModel;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackModel;
import fiji.plugin.trackmate.gui.panels.ActionListenablePanel;
import fiji.plugin.trackmate.util.ModelTools;
import org.jgrapht.graph.DefaultWeightedEdge;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Map;

public class ProofreadingPanel extends ActionListenablePanel {

    /*
     * FIELDS
     */

    private static final long serialVersionUID = 1L;

    protected Logger logger = Logger.IJ_LOGGER;

    private JLabel lblTrack;
    private JToggleButton buttonApprove;
    private static final String LABEL_NO_TRACKS = "No Tracks";
    private boolean shouldUpdateCenter;

    /**
     * Create the panel.
     */
    public ProofreadingPanel( final SelectionModel selectionModel, final Model model ) {
        setLayout( new GridLayout( 0, 1, 0, 0 ) );

        JPanel panel = new JPanel();
        panel.setBorder( new TitledBorder( null, "Track Inspector", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
        add( panel );
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{ 0, 0, 0, 0, 0 };
        gbl_panel.rowHeights = new int[]{ 0, 0, 0 };
        gbl_panel.columnWeights = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0 };
        gbl_panel.rowWeights = new double[]{ 0.0, 0.0, 0.0 };
        panel.setLayout( gbl_panel );

        JButton buttonFirst = new JButton( "<<" );
        buttonFirst.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( lblTrack.getText().equals( LABEL_NO_TRACKS ) || model.getTrackModel().nTracks( false ) == 0 ) {
                    return;
                }
                final TrackModel trackModel = model.getTrackModel();
                final int firstTrackID = trackModel.trackIDs( false ).iterator().next();
//				lblTrack.setText(trackModel.name(firstTrackID));
                setShouldUpdateCenter( true );
                selectionModel.clearSelection();
                selectionModel.selectTrack( trackModel.trackSpots( firstTrackID ), trackModel.trackEdges( firstTrackID ), 0 );
            }
        } );

        final JButton btnSelectWholeTrack = new JButton( "Select Whole Track" );
        btnSelectWholeTrack.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ModelTools.selectTrack( selectionModel );
            }
        } );
        GridBagConstraints gbc_btnSelectWholeTrack = new GridBagConstraints();
        gbc_btnSelectWholeTrack.weighty = 1.0;
        gbc_btnSelectWholeTrack.weightx = 1.0;
        gbc_btnSelectWholeTrack.insets = new Insets( 0, 0, 5, 5 );
        gbc_btnSelectWholeTrack.gridx = 2;
        gbc_btnSelectWholeTrack.gridy = 0;
        panel.add( btnSelectWholeTrack, gbc_btnSelectWholeTrack );
        GridBagConstraints gbc_buttonFirst = new GridBagConstraints();
        gbc_buttonFirst.weightx = 1.0;
        gbc_buttonFirst.weighty = 1.0;
        gbc_buttonFirst.insets = new Insets( 0, 0, 5, 5 );
        gbc_buttonFirst.gridx = 0;
        gbc_buttonFirst.gridy = 1;
        panel.add( buttonFirst, gbc_buttonFirst );

        JButton buttonPrev = new JButton( "<" );
        buttonPrev.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( lblTrack.getText().equals( LABEL_NO_TRACKS ) || model.getTrackModel().nTracks( false ) == 0 ) {
                    return;
                }
                String currentTrackName = lblTrack.getText();
                int previousTrackID = -1;
                final TrackModel trackModel = model.getTrackModel();
                Iterator< Integer > it = trackModel.trackIDs( false ).iterator();
                while ( it.hasNext() ) {
                    int trackID = it.next();
                    if ( trackModel.name( trackID ).equals( currentTrackName ) )
                        break;
                    previousTrackID = trackID;
                }
                if ( previousTrackID == -1 ) {
                    return;
                }
//				lblTrack.setText(trackModel.name(previousTrackID));
                setShouldUpdateCenter( true );
                selectionModel.clearSelection();
                selectionModel.selectTrack( trackModel.trackSpots( previousTrackID ), trackModel.trackEdges( previousTrackID ), 0 );
            }
        } );
        GridBagConstraints gbc_buttonPrev = new GridBagConstraints();
        gbc_buttonPrev.weighty = 1.0;
        gbc_buttonPrev.weightx = 1.0;
        gbc_buttonPrev.insets = new Insets( 0, 0, 5, 5 );
        gbc_buttonPrev.gridx = 1;
        gbc_buttonPrev.gridy = 1;
        panel.add( buttonPrev, gbc_buttonPrev );

        lblTrack = new JLabel();
        lblTrack.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent e ) {
                if ( lblTrack.getText().equals( LABEL_NO_TRACKS ) || model.getTrackModel().nTracks( false ) == 0 ) {
                    return;
                }
                final TrackModel trackModel = model.getTrackModel();
                int trackID = trackModel.trackIDOf( lblTrack.getText() );
                if ( buttonApprove != null ) {
                    try {
                        buttonApprove.setSelected( model.getTrackApprovedState( trackID ) );
                    } catch ( NullPointerException exception ) {
                        logger.log( "Track ID not found:" + trackID );
                    }
                }
            }
        } );
        lblTrack.setHorizontalAlignment( SwingConstants.CENTER );

        GridBagConstraints gbc_lblTrack = new GridBagConstraints();
        gbc_lblTrack.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTrack.weighty = 1.0;
        gbc_lblTrack.weightx = 1.0;
        gbc_lblTrack.insets = new Insets( 0, 0, 5, 5 );
        gbc_lblTrack.gridx = 2;
        gbc_lblTrack.gridy = 1;
        panel.add( lblTrack, gbc_lblTrack );

        JButton buttonNext = new JButton( ">" );
        buttonNext.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( lblTrack.getText().equals( LABEL_NO_TRACKS ) || model.getTrackModel().nTracks( false ) == 0 ) {
                    return;
                }
                String currentTrackName = lblTrack.getText();
                final TrackModel trackModel = model.getTrackModel();
                Iterator< Integer > it = trackModel.trackIDs( false ).iterator();
                while ( it.hasNext() ) {
                    if ( trackModel.name( it.next() ).equals( currentTrackName ) )
                        break;
                }
                if ( !it.hasNext() ) {
                    return;
                }
                final int nextTrackID = it.next();
//				lblTrack.setText(trackModel.name(nextTrackID));
                setShouldUpdateCenter( true );
                selectionModel.clearSelection();
                selectionModel.selectTrack( trackModel.trackSpots( nextTrackID ), trackModel.trackEdges( nextTrackID ), 0 );
            }
        } );
        GridBagConstraints gbc_buttonNext = new GridBagConstraints();
        gbc_buttonNext.weighty = 1.0;
        gbc_buttonNext.weightx = 1.0;
        gbc_buttonNext.insets = new Insets( 0, 0, 5, 5 );
        gbc_buttonNext.gridx = 3;
        gbc_buttonNext.gridy = 1;
        panel.add( buttonNext, gbc_buttonNext );

        JButton buttonLast = new JButton( ">>" );
        buttonLast.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( lblTrack.getText().equals( LABEL_NO_TRACKS ) || model.getTrackModel().nTracks( false ) == 0 ) {
                    return;
                }
                final TrackModel trackModel = model.getTrackModel();
                Iterator< Integer > it = trackModel.trackIDs( false ).iterator();
                int lastTrackID = it.next();
                while ( it.hasNext() ) {
                    lastTrackID = it.next();
                }
//				lblTrack.setText(trackModel.name(lastTrackID));
                setShouldUpdateCenter( true );
                selectionModel.clearSelection();
                selectionModel.selectTrack( trackModel.trackSpots( lastTrackID ), trackModel.trackEdges( lastTrackID ), 0 );
            }
        } );
        GridBagConstraints gbc_buttonLast = new GridBagConstraints();
        gbc_buttonLast.insets = new Insets( 0, 0, 5, 0 );
        gbc_buttonLast.weightx = 1.0;
        gbc_buttonLast.weighty = 1.0;
        gbc_buttonLast.gridx = 4;
        gbc_buttonLast.gridy = 1;
        panel.add( buttonLast, gbc_buttonLast );

        buttonApprove = new JToggleButton( "Approve" );
        buttonApprove.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( buttonApprove.isSelected() ) {
                    buttonApprove.setText( "Approved" );
                } else {
                    buttonApprove.setText( "Approve" );
                }
            }
        } );
        UIManager.put( "ToggleButton.select", Color.GREEN );
        SwingUtilities.updateComponentTreeUI( buttonApprove );
        buttonApprove.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( lblTrack.getText().equals( LABEL_NO_TRACKS ) || model.getTrackModel().nTracks( false ) == 0 ) {
                    return;
                }
                String currentTrackName = lblTrack.getText();
                final TrackModel trackModel = model.getTrackModel();
                Iterator< Integer > it = trackModel.trackIDs( false ).iterator();
                int trackID = -1;
                while ( it.hasNext() ) {
                    trackID = it.next();
                    if ( trackModel.name( trackID ).equals( currentTrackName ) )
                        break;
                }
                if ( trackID != -1 ) {
                    model.setTrackApprovedState( trackID, buttonApprove.isSelected() );
                }
            }
        } );

        GridBagConstraints gbc_buttonApprove = new GridBagConstraints();
        gbc_buttonApprove.weighty = 1.0;
        gbc_buttonApprove.weightx = 1.0;
        gbc_buttonApprove.insets = new Insets( 0, 0, 0, 5 );
        gbc_buttonApprove.gridx = 2;
        gbc_buttonApprove.gridy = 2;

        TrackModel trackModel = model.getTrackModel();
        int trackID = 0 < trackModel.nTracks( false ) ? trackModel.trackIDs( false ).iterator().next() : -1;
        lblTrack.setText( trackID != -1 ? trackModel.name( trackID ) : LABEL_NO_TRACKS );

        panel.add( buttonApprove, gbc_buttonApprove );
        model.addModelChangeListener( new ModelChangeListener() {

            @Override
            public void modelChanged( ModelChangeEvent event ) {
                Iterator< DefaultWeightedEdge > it = selectionModel.getEdgeSelection().iterator();
                if ( it.hasNext() ) {
                    final TrackModel trackModel = model.getTrackModel();
                    final int trackID = trackModel.trackIDOf( it.next() );
//					lblTrack.setText(trackModel.name(trackID));
                    selectionModel.clearSelection();
                    selectionModel.selectTrack( trackModel.trackSpots( trackID ), trackModel.trackEdges( trackID ), 0 );
                }
            }
        } );

        selectionModel.addSelectionChangeListener( new SelectionChangeListener() {
            @Override
            public void selectionChanged( SelectionChangeEvent event ) {
                final Map< Spot, Boolean > spots = event.getSpots();
                if ( spots != null ) {
                    final Iterator< Map.Entry< Spot, Boolean > > it = spots.entrySet().iterator();
                    while ( it.hasNext() ) {
                        final Map.Entry< Spot, Boolean > spotMap = it.next();
                        if ( spotMap.getValue().booleanValue() ) {
                            final Spot spot = spotMap.getKey();
                            final TrackModel trackModel = model.getTrackModel();
                            final Integer trackID = trackModel.trackIDOf( spot );
                            if ( trackID != null ) {
                                lblTrack.setText( trackModel.name( trackID ) );
                            }
                        }
                    }
                }
            }
        } );
    }

    public boolean shouldUpdateCenter() {
        return shouldUpdateCenter;
    }

    public void setShouldUpdateCenter( boolean shouldUpdateCenter ) {
        this.shouldUpdateCenter = shouldUpdateCenter;
    }
}
