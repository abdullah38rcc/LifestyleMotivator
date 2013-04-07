package com.example.lifestylemotivator.models;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LMActivityModel {
	private ArrayList<LMActivity> mActivityLst = new ArrayList<LMActivity>();

	/**
	 * Initialize the model from supplied xml file.
	 * @param filename
	 */
	public void initFmXmlFile(InputStream in) {
		mActivityLst.clear();

		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = null;
		try {
			doc = builder.parse(in, null);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("activity");

		for (int i = 0; i < nList.getLength(); i++) {

			Node nNode = nList.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				LMActivity activity = new LMActivity();

				activity.value = ((Element)nNode).getAttribute("value");

				mActivityLst.add(activity);
				NodeList offspring = nNode.getChildNodes();

				for(int j = 0; j < offspring.getLength(); j++) {
					Node n = offspring.item(j);
					if(n.getNodeType() == Node.ELEMENT_NODE) {
						NamedNodeMap atts = n.getAttributes();
						if(atts.getLength() > 0) {

							for (int k = 0; k < atts.getLength(); k++) {
								// Temp
								if(n.getNodeName().equals("temp")) {
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("mean")) {
										activity.meanTemp = Float.parseFloat(atts.item(k).getNodeValue());
									}
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("stddev")) {
										activity.stdDevTemp = Float.parseFloat(atts.item(k).getNodeValue());
									}
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("required")) {
										activity.isTempratureReqd = atts.item(k).getNodeValue().equals("y");
									}

								}
								// Humidity
								if(n.getNodeName().equals("humidity")) {
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("mean")) {
										activity.meanHumidity = Float.parseFloat(atts.item(k).getNodeValue());
									}
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("stddev")) {
										activity.stdDevHumidity = Float.parseFloat(atts.item(k).getNodeValue());
									}
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("required")) {
										activity.isHumidityReqd = atts.item(k).getNodeValue().equals("y");
									}
								}
								// windspeed
								if(n.getNodeName().equals("windspeed")) {
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("mean")) {
										activity.meanWindSpeed = Float.parseFloat(atts.item(k).getNodeValue());
									}
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("stddev")) {
										activity.stdDevWindSpeed = Float.parseFloat(atts.item(k).getNodeValue());
									}
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("required")) {
										activity.isWindSpeedReqd = atts.item(k).getNodeValue().equals("y");
									}
								}
								// Buddy
								if(n.getNodeName().equals("buddy")) {
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("required")) {
										activity.isBuddyReqd = atts.item(k).getNodeValue().equals("y");
									}
								}
								// Physical level required
								if(n.getNodeName().equals("physical")) {
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("required")) {
										activity.isPhysicalLevelReqd = atts.item(k).getNodeValue().equals("y");
									}
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("active")) {
										activity.isActive = atts.item(k).getNodeValue().equals("y");
									}
								}
								// Facility
								if(n.getNodeName().equals("facility")) {
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("required")) {
										activity.isFacilityReqd = atts.item(k).getNodeValue().equals("y");
									}
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("type")) {
										activity.facilityType = atts.item(k).getNodeValue();
									}
								}
								if(n.getNodeName().equals("mood")) {
									if(atts.item(k).getNodeName() != null 
											&& atts.item(k).getNodeName().equals("enhancing")) {
										activity.moodEnhancing = atts.item(k).getNodeValue().equals("y");
									}
								}

								//System.out.println("i=" + i + ":j=" + j + ":k=" + k + ":" + activity.value + ":"
								//       + nNode.getNodeName() + "==>"+ n.getNodeName() + "++"
								//		+ atts.item(k).getNodeName() + ":" + atts.item(k).getNodeValue());


							}

						}

					}

				}

			}
		}


	}

	/**
	 * Return a sorted list of activities depending on current context.
	 * @param ctxt
	 * @return suggested customized activities
	 */
	public ArrayList<LMActivity> sortActivityLst(LMCurrentCtxt ctxt) {
		// ---------------------------------------------------------------------------
		// Our secret formula for using the context to order the list.
		// Implements the set of rules for detecting favorable activities.
		// ---------------------------------------------------------------------------
		// Helper class
		class Pair implements Comparable {
			int score;
			int index;

			public Pair(int s, int i) {
				score = s;
				index = i;
			}

			@Override
			public int compareTo(Object arg0) {
				int s = ((Pair)arg0).score;
				return s > this.score ? -1 : s == this.score ? 0 : 1;
			}

		}

		// Sort the activity list. Decorate-sort-Undecorate
		ArrayList<Pair> pairLst = new ArrayList<Pair>();
		for(int i = 0; i < mActivityLst.size(); i++) {
			LMActivity activity = mActivityLst.get(i);
			if(activity.isDoable(ctxt)) {
				// Compute the standard score. The mean represents the ideal condition
				// and so a higher score means further out from the mean. This would 
				// make the activity less favorable.

				int score = activity.getStdScore(ctxt);
				//Store the result.
				pairLst.add(new Pair(score, i));
			}
		}
		// Sort it now
		Collections.sort(pairLst);

		// Undecorate for final output
		ArrayList<LMActivity> sortedLst = new ArrayList<LMActivity>();

		for(int i = 0; i < pairLst.size(); i++) {
			int index = pairLst.get(i).index;
			LMActivity activity = null;
			try {
				activity = (LMActivity) mActivityLst.get(index).clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Customize the msg. Update the original entry.
			activity.cutomizeMsg(ctxt);
			sortedLst.add(activity);
		}

		return sortedLst;  

	}
}
