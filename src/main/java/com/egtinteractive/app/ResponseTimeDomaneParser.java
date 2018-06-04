package com.egtinteractive.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.egtinteractive.config.FileLoader;

public class ResponseTimeDomaneParser<T> implements Parser<T> {

    private final String pattern;
    private Pattern patternDomane;
    private Pattern patternTime;
    private SimpleDateFormat format;

    private boolean isInProgress;
    private long time = -1;

    public ResponseTimeDomaneParser(final String pattern) {
	this.pattern = pattern;
    }

    public String getPattern() {
	return pattern;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T parseLine(final String line) {

	if (isThisLastLine(line)) {
	    return (T) new ResponseData(0, null, -1);
	}
	String domane;
	int response = 0;

	final long tmpTime = getTime(line);

	if (tmpTime != -1) {
	    time = tmpTime;
	    return null;
	}

	final String[] tmpData = getPerDomaineTimeResponse(line);

	if (tmpData != null) {
	    // TODO to be deleted Random() call
	    response = Integer.valueOf(tmpData[1]) + new Random().nextInt(233);
	    domane = tmpData[0];
	    final ResponseData rd = new ResponseData(response, domane, time);

	    return (T) rd;
	}

	return null;
    }

    private boolean isThisLastLine(final String line) {
	if (line == null) {
	    isInProgress = false;
	    return true;
	}
	if (!isInProgress) {
	    isInProgress = true;
	}
	return false;
    }

    private String[] getPerDomaineTimeResponse(final String line) {
	patternDomane = FileLoader.getPatternDomane();
	final Matcher matcher = patternDomane.matcher(line);
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
	patternTime = FileLoader.getPatternTime();
	final Matcher matcher = patternTime.matcher(line);
	if (matcher.find()) {
	    Date date;
	    try {
		format = FileLoader.getFormat();
		date = format.parse(matcher.group(1));
	    } catch (Exception e) {
		return -1;
	    }
	    return date.getTime();
	} else {
	    return -1;
	}
    }
}
