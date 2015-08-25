/*******************************************************************************
 * Copyright (c) 2015 Thales Global Services S.A.S.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *   Thales Global Services S.A.S - initial API and implementation
 ******************************************************************************/

package org.polarsys.kitalpha.ad.viewpoint.dsl.generation.diagram.mappingimport.merge;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.diagram.description.style.BeginLabelStyleDescription;
import org.eclipse.sirius.diagram.description.style.CenterLabelStyleDescription;
import org.eclipse.sirius.diagram.description.style.EdgeStyleDescription;
import org.eclipse.sirius.diagram.description.style.EndLabelStyleDescription;
import org.eclipse.sirius.viewpoint.description.style.BasicLabelStyleDescription;
import org.polarsys.kitalpha.ad.viewpoint.dsl.as.model.vpdiagram.EdgeDescription;
import org.polarsys.kitalpha.ad.viewpoint.dsl.as.model.vpdiagram.EdgeStyle;
import org.polarsys.kitalpha.ad.viewpoint.dsl.as.model.vpdiagram.Label;
import org.polarsys.kitalpha.ad.viewpoint.dsl.as.model.vpdiagram.VpdiagramPackage;

/**
 * @author Boubekeur Zendagui
 */
public final class EdgeStyleImportMerger {

	/**
	 * 
	 * @param originalStyle
	 * @param generatedStyle
	 * @param viewpointEdgeDescription
	 * @return
	 */
	public static EdgeStyleDescription mergeStyles(EdgeStyleDescription originalStyle, 
								   				   EdgeStyleDescription generatedStyle,
								   				   EdgeDescription viewpointEdgeDescription){
		/* Imported edge has either no style or contains only ConditionalStyles */
		if (originalStyle == null && generatedStyle != null)
			return generatedStyle;
		
		/* No style defined for the imported edge */
		if (generatedStyle == null)
			return null;
		
		EdgeStyleDescription result = generatedStyle;
		/* First: manage style data that are not generated by generation*/
		result = mergeNoGeneratedStyleData(originalStyle, generatedStyle);
		/* Second : Merge styles */
		result = mergeGeneratedStyleData(originalStyle, result, viewpointEdgeDescription);
		/* return result*/
		return result;
	}
	
	/**
	 * Import attribute value from original style if it was not set by user
	 * @param originalStyle
	 * @param generatedStyle
	 * @return
	 */
	private static EdgeStyleDescription mergeGeneratedStyleData(EdgeStyleDescription originalStyle, 
   			   													 EdgeStyleDescription generatedStyle,
   			   													 EdgeDescription viewpointEdgeDescription){
		/** Initialize result by generated style*/
		EdgeStyleDescription result = generatedStyle;
		
		/** Do Merge Attributes */
		final EdgeStyle viewpointEdgeStyle = viewpointEdgeDescription.getStyle();
		if (! viewpointEdgeStyle.eIsSet(VpdiagramPackage.eINSTANCE.getEdgeStyle_BeginDecorator()))
			result.setSourceArrow(originalStyle.getSourceArrow());
		
		if (! viewpointEdgeStyle.eIsSet(VpdiagramPackage.eINSTANCE.getEdgeStyle_EndDecorator()))
			result.setTargetArrow(originalStyle.getTargetArrow());
		
		if (! viewpointEdgeStyle.eIsSet(VpdiagramPackage.eINSTANCE.getEdgeStyle_LineStyle()))
			result.setLineStyle(originalStyle.getLineStyle());
		
		/** Do Merge References */
		if (! viewpointEdgeStyle.eIsSet(VpdiagramPackage.eINSTANCE.getEdgeStyle_Color()))
			result.setStrokeColor(originalStyle.getStrokeColor());
		
		// Do merge Begin Label
		BasicLabelStyleDescription beginLabel =  mergeLabelDescription(originalStyle.getBeginLabelStyleDescription(), 
																       generatedStyle.getBeginLabelStyleDescription(), 
																       viewpointEdgeDescription.getBegin_Label());
		if (beginLabel != null)
			result.setBeginLabelStyleDescription((BeginLabelStyleDescription)beginLabel);
		
		// Do merge Center Label
		BasicLabelStyleDescription centerLabel = mergeLabelDescription(originalStyle.getCenterLabelStyleDescription(), 
																       generatedStyle.getCenterLabelStyleDescription(), 
																       viewpointEdgeDescription.getCenter_label());
		if (centerLabel != null)
			result.setCenterLabelStyleDescription((CenterLabelStyleDescription)centerLabel);
		
		// Do merge End Label
		BasicLabelStyleDescription endLabel = mergeLabelDescription(originalStyle.getEndLabelStyleDescription(), 
																       generatedStyle.getEndLabelStyleDescription(), 
																       viewpointEdgeDescription.getEnd_label());
		if (endLabel != null)
			result.setEndLabelStyleDescription((EndLabelStyleDescription)endLabel);
		
		return result;
	}
	
	/**
	 * 
	 * @param originalLabel
	 * @param generatedLabel
	 * @return
	 */
	private static BasicLabelStyleDescription mergeLabelDescription(BasicLabelStyleDescription originalLabel,
															 		BasicLabelStyleDescription generatedLabel,
															 		Label viewpointLabel){
		
		/** 
		 * If original Label is null, that means imported Edge has no Begin Label Description. So return the generated Label.
		 * the generated Label is not null only if it was specified by user. 
		*/
		if (originalLabel == null)
			return generatedLabel;
		else
		{
			if (generatedLabel == null)
				return EcoreUtil.copy(originalLabel);
		}
		
		/** If both label are equal, so return the generated one */
		if (originalLabel.equals(generatedLabel) || (viewpointLabel == null && generatedLabel != null))
			return generatedLabel;
		
		/** If we are here, this means that the both labels exist, so let do merge ;) */
		
		BasicLabelStyleDescription result = generatedLabel;
		
		// Manage NO generated label style data 
		result.setShowIcon(originalLabel.isShowIcon());
		result.setIconPath(originalLabel.getIconPath());
		
		// Manage generated label style data 
		if (! viewpointLabel.eIsSet(VpdiagramPackage.eINSTANCE.getLabel_Color()))
			result.setLabelColor(originalLabel.getLabelColor());
		
		if (! viewpointLabel.eIsSet(VpdiagramPackage.eINSTANCE.getLabel_Value()))
			result.setLabelExpression(originalLabel.getLabelExpression());
		
		if (! viewpointLabel.eIsSet(VpdiagramPackage.eINSTANCE.getLabel_Size()))
			result.setLabelSize(originalLabel.getLabelSize());
		
		if (! (viewpointLabel.eIsSet(VpdiagramPackage.eINSTANCE.getLabel_Bold()) && 
			   viewpointLabel.eIsSet(VpdiagramPackage.eINSTANCE.getLabel_Italic())))
		{
			result.getLabelFormat().clear();
			result.getLabelFormat().addAll(originalLabel.getLabelFormat());
		}
		return result;
	}
	
	/**
	 * Import attribute values from original style of every no generated one
	 * @param originalStyle
	 * @param generatedStyle
	 * @return
	 */
	private static EdgeStyleDescription mergeNoGeneratedStyleData(EdgeStyleDescription originalStyle, 
			   									       			   EdgeStyleDescription generatedStyle){
		EdgeStyleDescription result = generatedStyle;
		result.setRoutingStyle(originalStyle.getRoutingStyle());
		result.setSizeComputationExpression(originalStyle.getSizeComputationExpression());
		result.setFoldingStyle(originalStyle.getFoldingStyle());
		result.setEndsCentering(originalStyle.getEndsCentering());
		return result;
		
	}
	
}
