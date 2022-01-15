package de.mrg4ming.data;

public enum WindowTitle {

    PAGE("§6Shop §8page §7"),
    TRADE_OPTIONS_PREFIX("§dTrade options: §6"),
    TRADE_PREFIX("§dTrade: §6"),
    TRADE_EDITOR_PREFIX("§bEditor: §6");

    public final String title;

    private WindowTitle(String _title) {
        this.title = _title;
    }

}
