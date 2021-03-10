package com.tutorial.main;

//this interface is used to give each specific menu subclass the choiceID method
@FunctionalInterface
public interface ChoiceProcessorInterface {
	void processChoice(String choiceID);
}
