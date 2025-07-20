package hood.manager.acid.setting;

import hood.manager.acid.module.Module;

import java.util.ArrayList;

public class SettingsManager {
	
	private ArrayList<Setting> settings;
	
	public SettingsManager(){
		this.settings = new ArrayList<Setting>();
	}
	
	public void addSetting(Setting in){
		if (in == null) System.err.println("setting is null");
		this.settings.add(in);
	}
	
	public ArrayList<Setting> getSettings(){
		return this.settings;
	}
	
	public ArrayList<Setting> getSettingsByMod(Module mod){
		ArrayList<Setting> out = new ArrayList<Setting>();
		for(Setting s : getSettings()){
			if(s.getParentMod().equals(mod)){
				out.add(s);
			}
		}
		if(out.isEmpty()){
			return null;
		}
		return out;
	}
	
	public Setting getSettingByName(Module mod, String name){
		for(Setting set : getSettings()){
			if(set.getName().equalsIgnoreCase(name) && set.getParentMod() == mod){
				return set;
			}
		}
		System.err.println("no setting with name: " + name);
		return null;
	}
}