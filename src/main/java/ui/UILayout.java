package ui;

public class UILayout {
    public int activeMainTab;

    public int activeRightTab;

    public UILayout(int mainTabIdx, int rightTabIdx) {
        activeMainTab = mainTabIdx;
        activeRightTab = rightTabIdx;
    }
}
