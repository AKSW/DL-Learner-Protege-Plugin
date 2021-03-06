/**
 * Copyright (C) 2007-2009, Jens Lehmann
 *
 * This file is part of DL-Learner.
 * 
 * DL-Learner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * DL-Learner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.dllearner.tools.protege;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboPopup;

import org.dllearner.core.EvaluatedDescription;
import org.dllearner.learningproblems.EvaluatedDescriptionClass;
import org.dllearner.reasoning.ClosedWorldReasoner;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 * This class takes care of all events happening in the GraphicalCoveragePanel.
 * It renders the Informations for the individual points and sets the
 * individuals for the popup component.
 * 
 * @author Christian Koetteritzsch
 * 
 */
public class GraphicalCoveragePanelHandler implements MouseMotionListener,
		MouseListener, PropertyChangeListener {

	private final GraphicalCoveragePanel panel;
	private EvaluatedDescription description;
	private BasicComboPopup scrollPopup;
	private final Vector<String> individualComboBox;
	private JComboBox indiBox;
	private OWLOntology ontology;

	/**
	 * This is the constructor for the handler.
	 * 
	 * @param p
	 *            GraphicalCoveragePanel
	 * @param eval
	 *            EvaluatedDescription
	 */
	public GraphicalCoveragePanelHandler(GraphicalCoveragePanel p,
			EvaluatedDescription eval) {
		this.panel = p;
		description = eval;
		ontology = Manager.getInstance().getActiveOntology();
		individualComboBox = new Vector<>();

	}
	
	public void setDescription(EvaluatedDescription description){
		this.description = description;
	}

	@Override
	/**
	 * Nothing happens here.
	 */
	public void mouseDragged(MouseEvent arg0) {

	}

	@Override
	/**
	 * This methode renders the tool tip message when the mouse goes over
	 * the plus symbole. It also renders the the informations for the individual point.
	 */
	public void mouseMoved(MouseEvent m) {
		if (m.getX() >= panel.getX1() + panel.getShiftCovered()
				&& m.getX() <= panel.getX2() + panel.getShiftCovered()
				&& m.getY() >= panel.getY1() && m.getY() <= panel.getY2()
				|| m.getX() >= panel.getX1() + panel.getShiftNewConcept()
				&& m.getX() <= panel.getX2() + panel.getShiftNewConcept()
				&& m.getY() >= panel.getY1() && m.getY() <= panel.getY2()
				|| m.getX() >= panel.getX1() + panel.getShiftNewConceptX()
				&& m.getX() <= panel.getX2() + panel.getShiftNewConceptX()
				&& m.getY() >= panel.getY1() + panel.getShiftNewConcept()
				&& m.getY() <= panel.getY2() + panel.getShiftNewConcept()
				|| m.getX() >= panel.getX1() - panel.getShiftOldConcept()
				&& m.getX() <= panel.getX2() - panel.getShiftOldConcept()
				&& m.getY() >= panel.getY1() && m.getY() <= panel.getY2()) {
			panel.getGraphicalCoveragePanel().setToolTipText(
					"To view all Individuals please click on the plus");
		}

		Vector<IndividualPoint> v = panel.getIndividualVector();
		ClosedWorldReasoner reasoner = Manager.getInstance().getReasoner();
		for (int i = 0; i < v.size(); i++) {
			if (v.get(i).getXAxis() >= m.getX() - 5
					&& v.get(i).getXAxis() <= m.getX() + 5
					&& v.get(i).getYAxis() >= m.getY() - 5
					&& v.get(i).getYAxis() <= m.getY() + 5) {
				StringBuilder individualInformation = new StringBuilder("<html><body><b>"
						+ v.get(i).getIndividualName() + "</b>");
				if (v.get(i).getIndividual() != null) {

					Set<OWLClass> types = reasoner.getTypes(v.get(i)
							.getIndividual());
					individualInformation.append("<br><br><b>Types:</b><br>");
					for (OWLClass dlLearnerClass : types) {
						individualInformation.append(Manager.getInstance().getRendering(dlLearnerClass)).append("<br>");
					}
					Map<OWLObjectProperty, Set<OWLIndividual>> op2Individuals = reasoner.getObjectPropertyRelationships(v.get(i).getIndividual());
					Set<OWLObjectProperty> key = op2Individuals.keySet();
					individualInformation.append("<br><b>Object Property Values:</b><br>");
					
					for (Entry<OWLObjectProperty, Set<OWLIndividual>> entry : op2Individuals.entrySet()) {
						OWLObjectProperty op = entry.getKey();
						individualInformation.append(Manager.getInstance().getRendering(op)).append(" ");
						
						Set<OWLIndividual> individuals = entry.getValue();
						for (OWLIndividual ind : individuals) {
							individualInformation.append(Manager.getInstance().getRendering(ind));
							if (individuals.size() > 1) {
								individualInformation.append(", ");
							}
						}
						individualInformation.append("<br>");
						
					}
				
					if (v.get(i).getIndividual() != null) {
						Set<OWLDataPropertyAssertionAxiom> dataProperties = ontology
								.getDataPropertyAssertionAxioms(v.get(i)
										.getIndividual());
						individualInformation.append("<br><b>Data Property Values</b><br>");
						for (OWLDataPropertyAssertionAxiom dataProperty : dataProperties) {
							individualInformation.append(dataProperty.toString()).append("<br>");
						}
						
						Set<OWLNegativeObjectPropertyAssertionAxiom> negObjects = ontology.getNegativeObjectPropertyAssertionAxioms(v.get(i).getIndividual());
						individualInformation.append("<br><b>negative Object Properties</b><br>");
						for (OWLNegativeObjectPropertyAssertionAxiom negObject : negObjects) {
							individualInformation.append(negObject.toString()).append("<br>");
						}
						
						Set<OWLNegativeDataPropertyAssertionAxiom> negDatas = ontology.getNegativeDataPropertyAssertionAxioms(v.get(i).getIndividual());
						individualInformation.append("<br><b>negative Dataproperties</b><br>");
						for (OWLNegativeDataPropertyAssertionAxiom negData : negDatas) {
							individualInformation.append(negData.toString()).append("<br>");
						}
						Collection<OWLIndividual> sameIndies = EntitySearcher.getSameIndividuals(v.get(i).getIndividual(), ontology);
						individualInformation.append("<br><b>Same Individuals</b><br>");
						for (OWLIndividual sameIndie : sameIndies) {
							individualInformation.append(sameIndie.toString()).append("<br>");
						}
						
						Set<OWLDifferentIndividualsAxiom> differentIndies = ontology.getDifferentIndividualAxioms(v.get(i).getIndividual());
						individualInformation.append("<br><b>Different Individuals</b><br>");
						for (OWLDifferentIndividualsAxiom differentIndie : differentIndies) {
							individualInformation.append(differentIndie.toString()).append("<br>");
						}
					}
				}
				individualInformation.append("</body></htlm>");
				panel.getGraphicalCoveragePanel().setToolTipText(
						individualInformation.toString());
			}
		}
	}

	@Override
	/**
	 * Nothing happens here.
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
	}

	@Override
	/**
	 * This methode renders the popup box and
	 * computes which individuals must be shown.
	 */
	public void mouseClicked(MouseEvent arg0) {
		if (panel.getEvaluateddescription() != null) {
			if (arg0.getX() >= panel.getX1() + panel.getShiftCovered()
					&& arg0.getX() <= panel.getX2() + panel.getShiftCovered()
					&& arg0.getY() >= panel.getY1()
					&& arg0.getY() <= panel.getY2()) {

				individualComboBox.clear();

				Set<OWLIndividual> covInd = ((EvaluatedDescriptionClass) description)
						.getCoveredInstances();
				int i = covInd.size();
				if (i > 0) {
					for (OWLIndividual ind : covInd) {
						individualComboBox.add(Manager.getInstance().getRendering(ind));
					}
					indiBox = new JComboBox(individualComboBox);
					scrollPopup = new BasicComboPopup(indiBox);
					scrollPopup.setAutoscrolls(true);
					scrollPopup.show(panel, arg0.getX(), arg0.getY());
				}
			}

			if (arg0.getX() >= panel.getX1() + panel.getShiftNewConcept()
					&& arg0.getX() <= panel.getX2()
							+ panel.getShiftNewConcept()
					&& arg0.getY() >= panel.getY1()
					&& arg0.getY() <= panel.getY2()
					|| arg0.getX() >= panel.getX1()
							+ panel.getShiftNewConceptX()
					&& arg0.getX() <= panel.getX2()
							+ panel.getShiftNewConceptX()
					&& arg0.getY() >= panel.getY1()
							+ panel.getShiftNewConcept()
					&& arg0.getY() <= panel.getY2()
							+ panel.getShiftNewConcept()) {

				individualComboBox.clear();
				Set<OWLIndividual> addInd = ((EvaluatedDescriptionClass) description)
						.getAdditionalInstances();
				int i = addInd.size();
				if (i > 0) {
					for (OWLIndividual ind : addInd) {
						individualComboBox.add(Manager.getInstance().getRendering(ind));
					}
					indiBox = new JComboBox(individualComboBox);
					scrollPopup = new BasicComboPopup(indiBox);
					scrollPopup.setAutoscrolls(true);
					scrollPopup.show(panel, arg0.getX(), arg0.getY());
				}
			}

			if (arg0.getX() >= panel.getX1() - panel.getShiftOldConcept()
					&& arg0.getX() <= panel.getX2()
							- panel.getShiftOldConcept()
					&& arg0.getY() >= panel.getY1()
					&& arg0.getY() <= panel.getY2()) {

				individualComboBox.clear();
				Set<OWLIndividual> notCovInd = Manager.getInstance().getIndividuals();
				notCovInd.removeAll(((EvaluatedDescriptionClass) description)
						.getCoveredInstances());
				int i = notCovInd.size();
				if (i > 0) {
					for (OWLIndividual ind : notCovInd) {
						individualComboBox.add(Manager.getInstance().getRendering(ind));
					}
					indiBox = new JComboBox(individualComboBox);
					scrollPopup = new BasicComboPopup(indiBox);
					scrollPopup.setAutoscrolls(true);
					scrollPopup.show(panel, arg0.getX(), arg0.getY());
				}
			}
		}
	}

	@Override
	/**
	 * Nothing happens here.
	 */
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	/**
	 * Nothing happens here.
	 */
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	/**
	 * Nothing happens here.
	 */
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	/**
	 * Nothing happens here.
	 */
	public void mouseReleased(MouseEvent arg0) {

	}

}
