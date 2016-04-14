package joshie.enchiridion.gui.book.features;

import com.google.gson.JsonObject;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.helpers.JSONHelper;
import joshie.enchiridion.helpers.JumpHelper;

public class FeatureJump extends FeatureAbstract {
    public transient IPage page;
    private transient int number;
    private transient String jumpTo;

    public FeatureJump(){}
    public FeatureJump(int number, String jumpTo) {
        this.number = number;
        this.jumpTo = jumpTo;
    }

    @Override
    public FeatureJump copy() {
        return new FeatureJump(number, jumpTo);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (page == null) {
            if (jumpTo != null && !jumpTo.equals("#LEGACY#")) {
                if (page == null) {
                    try {
                        page = JumpHelper.getPageByNumber(GuiBook.INSTANCE.getBook(), Integer.parseInt(jumpTo));
                    } catch (Exception e) {}
                }
            } else {
                page = JumpHelper.getPageByNumber(GuiBook.INSTANCE.getBook(), number);
            }
        }
    }

    @Override
    public void performClick(int mouseX, int mouseY) {
        EnchiridionAPI.book.setPage(page);
    }

    @Override
    public void readFromJson(JsonObject json) {
        number = JSONHelper.getIntegerIfExists(json, "number");
        if (json.get("jumpTo") != null) {
            jumpTo = JSONHelper.getStringIfExists(json, "jumpTo");
        } else jumpTo = "#LEGACY#";
    }

    @Override
    public void writeToJson(JsonObject object) {
        if (page != null) {
            object.addProperty("number", page.getPageNumber());
        }
    }
}
