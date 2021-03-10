package com.tutorial.main;

public class MenuDataArchive {
	
	private String promptString;
	public String getPromptString() {
		return this.promptString;
	}
	
	private String[][] optionStrings;
	public String[][] getOptionStrings() {
		return this.optionStrings;
	}
	
	private ChoiceProcessorInterface archFunction;
	public ChoiceProcessorInterface getArchFunction() {
		return this.archFunction;
	}
	
	private ID menuType;
	public ID getMenuType() {
		return this.menuType;
	}
	
	private String menuTitle;
	public String getMenuTitle() {
		return this.menuTitle;
	}
	MenuDataArchive(String setArchPromptString,
			String[][] setOptionStrings,
			ChoiceProcessorInterface setArchFunction,
			ID setMenuType, 
			String setMenuTitle) {
		
		this.promptString = setArchPromptString;
		this.optionStrings = setOptionStrings;
		this.archFunction = setArchFunction;
		this.menuType = setMenuType;
		this.menuTitle = setMenuTitle;
	}
	
}
