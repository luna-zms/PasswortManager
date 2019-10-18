package util;

import java.util.EnumSet;

public class PasswordGeneratorSettings {
    private int length = 1;
    private EnumSet<CharGroup> charGroups = EnumSet.noneOf(CharGroup.class);

	public void selectCharGroup(CharGroup group) {
        charGroups.add(group);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public EnumSet<CharGroup> getCharGroups() {
        return charGroups;
    }
}
