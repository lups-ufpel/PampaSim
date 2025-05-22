package org.pampasim;

public record CreateProcessRecord(int start,
                                  int duration,
                                  int priority,
                                  String color) { }