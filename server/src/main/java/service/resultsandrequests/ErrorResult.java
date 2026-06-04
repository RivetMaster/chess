package service.resultsandrequests;

public record ErrorResult(Exception Error) implements Result{}