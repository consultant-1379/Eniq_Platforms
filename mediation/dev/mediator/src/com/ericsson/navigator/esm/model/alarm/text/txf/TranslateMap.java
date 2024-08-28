package com.ericsson.navigator.esm.model.alarm.text.txf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.util.file.FileListener;
import com.ericsson.navigator.esm.util.file.FileMonitor;

public class TranslateMap implements FileListener {
	
	private static final String classname = TranslateMap.class.getName();
	private static Logger logger = Logger.getLogger(classname);
	
	private final File file;
	private final Map<String, Map<String, Map<String, String>>> translationMap;
	private final FileMonitor fileMonitor;
	private boolean conversionApplied = false;
	
	public TranslateMap(final File mapFile, final long pollDelay) {
		file = mapFile;
		translationMap = new TreeMap<String, Map<String, Map<String, String>>>();
		fileMonitor = new FileMonitor(pollDelay);
	}

	public TranslatedAlarm translate(final Alarm alarm) {
		final String orgSpecificProblem = alarm.getSpecificProblem();
		final String specificProblem = translate(orgSpecificProblem, "SpecificProblem");
		final String eventType = translate(alarm.getEventType(), "EventType");
		final String probableCause = translate(alarm.getProbableCause(), "ProbableCause");
		final TranslatedAlarm translatedAlarm = new TranslatedAlarm(alarm);
		translatedAlarm.setSpecificProblem(specificProblem);
		translatedAlarm.setEventType(eventType);
		translatedAlarm.setProbableCause(probableCause);
		applyConversions(orgSpecificProblem, translatedAlarm);
		if (alarm instanceof TXFAlarm){
			final TXFAlarm txf = (TXFAlarm)alarm;
			final String proposedRepairAction = translate(txf.getProposedRepairAction(), "ProposedRepairAction");
			if(proposedRepairAction != null && !(proposedRepairAction.equals(""))){
				if(!conversionApplied){
					translatedAlarm.addTranslatedIntToAdditionalText("ProposedRepairAction", proposedRepairAction);
				}
			}
		}
		translatedAlarm.addTranslatedIntToAdditionalText("SP_INT", orgSpecificProblem);
		translatedAlarm.addTranslatedIntToAdditionalText("ET_INT", alarm.getEventType());
		translatedAlarm.addTranslatedIntToAdditionalText("PC_INT", alarm.getProbableCause());
		translatedAlarm.renewUniqueId();
		conversionApplied = false;
		return translatedAlarm;
	}

	private void applyConversions(final String value, final TranslatedAlarm translatedAlarm) {
		final Map<String, String> matchedMap = getTranslateValues(value, "SpecificProblem");
		if(matchedMap == null){
			return;
		}
		final String mappendValue = matchedMap.get("conversions");
		if(mappendValue!=null){
			final String etValue = getConversionedValue("TXF_SetET", "EventType", mappendValue);
			if(etValue != null){
				translatedAlarm.setEventType(etValue);
			}
			final String pcValue = getConversionedValue("TXF_SetPC", "ProbableCause", mappendValue);
			if(pcValue != null){
				translatedAlarm.setProbableCause(pcValue);
			}
			final String praValue = getConversionedValue("TXF_SetPRA", "ProposedRepairAction", mappendValue);
			if(praValue != null){
				conversionApplied = true;
				translatedAlarm.setProposedRepairAction(praValue);
			}
		}
	}

	private String getConversionedValue(final String ruleName, final String attributeName,
			final String mappendValue) {

		final int ruleIndex = mappendValue.indexOf(ruleName);
		if(ruleIndex == -1){
			return null;
		}
		final int endOfRule = ruleIndex+ruleName.length();
		final int startValueIndex = mappendValue.indexOf('(', endOfRule)+1;
		final int endValueIndex = mappendValue.indexOf(',', endOfRule);
		if(startValueIndex == -1 || endValueIndex == -1){
			return null;
		}
		final String etValue = mappendValue.substring(startValueIndex, endValueIndex);
		return translate(etValue.trim(), attributeName);
	}

	private synchronized String translate(final String value, final String attributeName) {
		final Map<String, String> matchedMap = getTranslateValues(value, attributeName);
		if(matchedMap == null){
			return value;
		}
		final String mappendValue = matchedMap.get("text");
		return mappendValue;
	}
	
	private synchronized Map<String, String> getTranslateValues(final String value, final String attributeName) {
		final Map<String, Map<String, String>> attributeMap = translationMap.get(attributeName);

		if(attributeMap == null){
			return null;
		}
		return attributeMap.get(value);
	}
	
	public void initialize(){
		fileMonitor.addFile(file);
		fileMonitor.addListener(this);
	}
	
	public void cancel(){
		fileMonitor.removeListener(this);
		fileMonitor.removeFile(file);
		fileMonitor.stop();
	}

	/**
	 * SpecificProblem$0 p \
     : no,i, 1009 \
     : text,s, "SunMC agent: The % CPU time the SunMC agent has gone over the rule limit." \
     : pattern,s, \
     : conversions,s,"TXF_SetET(4000,ABS)"
	 */
	public synchronized void load() throws IOException {
		logger.debug(classname + ".load(); --> translatefile: " + file);
		translationMap.clear();
		BufferedReader bReader = null;
		try{
			final FileReader reader = new FileReader(file);
			bReader = new BufferedReader(reader);
			String line = null;
			StringBuffer currentLine = null;
			while((line = bReader.readLine()) != null){
				if(currentLine == null){
					currentLine = new StringBuffer(removeLineContinuation(line)); //NOPMD
				} else {
					currentLine.append(removeLineContinuation(line));
				}
				if(!line.trim().endsWith("\\")){
					if(!line.startsWith("#")){
						parseTranslation(currentLine.toString());
					}
					currentLine = null;
				}
			}
		} finally {
			if(bReader != null){
				bReader.close();
			}
			logger.debug(classname + ".load(); <-- translatefile: " + file);
		}
	}

	private String removeLineContinuation(String line) {
		line = line.trim();
		if(line.endsWith("\\")){
			return line.substring(0, line.lastIndexOf('\\'));
		}
		return line;
	}

	/**
	 * SpecificProblem$0 p : no,i, 1009 : text,s, "SunMC agent: The % CPU time the SunMC agent has gone over the rule limit." : pattern,s, : conversions,s,"TXF_SetET(4000,ABS)"
	 */
	private void parseTranslation(final String line) throws IOException {
		try{
			final StringTokenizer translation = new StringTokenizer(line, ":\"", true);
			if(!translation.hasMoreTokens()){
				return;
			}
			final String alarmAttributeName = getTranslationName(translation.nextToken());
			if(alarmAttributeName == null){
				return;
			}
			Map<String, Map<String, String>> attributeMap = translationMap.get(alarmAttributeName);
			if(!ignoreNextToken(translation)){
				return;
			}
			if(!translation.hasMoreTokens()){
				return;
			}
			if(attributeMap == null){
				attributeMap = new TreeMap<String, Map<String, String>>();
				translationMap.put(alarmAttributeName, attributeMap);
			}
			final String integer = getValue(translation.nextToken());
			final Map<String, String> translationValues = new TreeMap<String, String>();
			attributeMap.put(integer, translationValues);
			if(!ignoreNextToken(translation)){
				return;
			}
			parseMappedValues(translation, translationValues);
		}catch (final NoSuchElementException e){
			throw new IOException("Failed to parse translatemap " + 
					file.getAbsolutePath() + ", line: " + line, e);
		}
	}

	private void parseMappedValues(final StringTokenizer translation,
			final Map<String, String> translationValues) {
		StringBuffer fullLineBuffer = new StringBuffer();
		boolean inString = false;
		while(translation.hasMoreTokens()){
			final String token = translation.nextToken();
			if(token.equals("\"")){
				inString = !inString;
			} else if(!inString && token.equals(":")){
				parseEntry(fullLineBuffer.toString(), translationValues);
				fullLineBuffer = new StringBuffer(); //NOPMD
			} else {
				fullLineBuffer.append(token);
			}
		}
		parseEntry(fullLineBuffer.toString(), translationValues);
	}

	private boolean ignoreNextToken(final StringTokenizer tokenizer) {
		final boolean hasMore = tokenizer.hasMoreTokens();
		tokenizer.nextToken();
		return hasMore;
	}

	private void parseEntry(final String entry, final Map<String, String> translationValues) {
		final String id = getIdentifier(entry);
		final String value = getValue(entry);
		if(id == null || value == null){
			return;
		}
		translationValues.put(id, value);
	}

	private String getTranslationName(final String value) {
		final int index = value.indexOf('$');
		if(index == -1){
			return null;
		}
		return value.substring(0, index);
	}

	private String getIdentifier(final String text) {
		final int index = text.indexOf(',');
		if(index == -1){
			return null;
		}
		return text.substring(0, text.indexOf(',')).trim();
	}

	private String getValue(final String text) {
		return text.substring(text.indexOf(',', text.indexOf(',')+1)+1).replace('"', ' ').trim();
	}

	public void fileChanged(final File fileToLoad) {
		try {
			load();
		} catch (IOException e) {
			logger.error(classname + 
					".fileChanged(); IO Error occured reading changed TXF translation file: " + 
					fileToLoad.getAbsolutePath(), e);
		}
	}
	
	@Override
	public String toString() {
		return file.getAbsolutePath();
	}

}
