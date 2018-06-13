package com.egtinteractive.app.parsers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.egtinteractive.app.moduls.ResponseData;

public class ResponseTimeDomainParser<T> implements Parser<T> {

    private final Pattern patternDomain;
    private final Pattern patternTime; 
    private final String dateTimeFormat; 

    private long time = -1;
    
    public ResponseTimeDomainParser(final String patternDomain, final String patternTime, final String format) {
	this.patternDomain = Pattern.compile(patternDomain);
	this.patternTime   = Pattern.compile(patternTime);
	this.dateTimeFormat = format;
    } 

    @SuppressWarnings("unchecked")
    @Override
    public T parseLine(final String line) {
	if (line == null) {
	    return null; 
	}
	String domain;
	int response = 0;

	final long tmpTime = getTime(line);

	if (tmpTime != -1) {
	    time = tmpTime;
	    return null;
	} 
	final String[] tmpData = getPerDomainTimeResponse(line);

	if (tmpData != null) {
	    response = Integer.valueOf(tmpData[1]);
	    domain = tmpData[0];
	    final ResponseData rd = new ResponseData(response, domain, time);

	    return (T) rd;
	}
	return null;
    } 

    private String[] getPerDomainTimeResponse(final String line) {
	final Matcher matcher = patternDomain.matcher(line);
	final String[] result = new String[2];
	if (matcher.find()) {
	    result[0] = matcher.group(1).trim();
	    result[1] = matcher.group(2).trim();
	    return result;
	} else {
	    return null;
	}
    }

    private long getTime(final String line) {
	final Matcher matcher = patternTime.matcher(line);
	if (matcher.find()) {
	    Date date;
	    try {
		date = new SimpleDateFormat(dateTimeFormat).parse(matcher.group(1));
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
	    return date.getTime();
	} else {
	    return -1;
	}
    }
}
