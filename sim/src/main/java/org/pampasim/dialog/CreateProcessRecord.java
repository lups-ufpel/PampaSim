package org.pampasim.dialog;

public record CreateProcessRecord(int start,
                                  int duration,
                                  int priority,
                                  String color) { }