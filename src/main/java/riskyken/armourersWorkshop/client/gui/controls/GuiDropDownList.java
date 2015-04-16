package riskyken.armourersWorkshop.client.gui.controls;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDropDownList extends GuiButtonExt {

    private List<String> listItems;
    private int selectedIndex;
    private int hoverIndex;
    private boolean isMouseDownOver;
    private boolean isDroppedDown;
    private int dropButtonX;
    private int dropButtonY;
    private int dropButtonWidth;
    private int dropButtonHeight;
    private IDropDownListCallback callback;
    
    public GuiDropDownList(int id, int xPos, int yPos, int width, String displayString, IDropDownListCallback callback) {
        super(id, xPos, yPos, width, 14, displayString);
        this.callback = callback;
        this.listItems = new ArrayList<String>();
        this.selectedIndex = 0;
        this.hoverIndex = -1;
        this.isMouseDownOver = false;
        this.isDroppedDown = false;
        this.dropButtonHeight = this.height;
        this.dropButtonWidth = 14;
        this.dropButtonY = this.yPosition;
        this.dropButtonX = this.xPosition + this.width - this.dropButtonWidth;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            
            drawDropDownButton(mc, mouseX, mouseY);
            
            this.hoverIndex = -1;
            if (this.isDroppedDown) {
                int listSize = listItems.size();
                GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition + this.height + 1, 0, 46, this.width, 10 * listSize + 4, 200, 20, 2, 3, 2, 2, this.zLevel);
                for (int i = 0; i < listSize; i++) {
                    String listItem = listItems.get(i);
                    int textX = this.xPosition + 4;
                    int textY = this.yPosition + this.height + 4 + (i * 10);
                    int textWidth = mc.fontRenderer.getStringWidth(listItem);
                    int textHeight = 10;
                    int textColour = 16777215;
                    if (mouseX >= textX && mouseY >= textY && mouseX < textX + textWidth && mouseY < textY + textHeight) {
                        textColour = 16777120;
                        this.hoverIndex = i;
                    }
                    mc.fontRenderer.drawString(listItem, textX, textY, textColour);
                } 
            }
            
            mc.fontRenderer.drawString(this.displayString, this.xPosition + 3, this.yPosition + 3, 16777215);
        }
    }
    
    private boolean mouseOverDropDownButton(int mouseX, int mouseY) {
        return mouseX >= this.dropButtonX && mouseY >= this.dropButtonY && mouseX < this.dropButtonX + this.dropButtonWidth && mouseY < this.dropButtonY + this.dropButtonHeight;
    }
    
    private void drawDropDownButton(Minecraft mc, int mouseX, int mouseY) {
        int k = this.getHoverState(mouseOverDropDownButton(mouseX, mouseY));
        GuiUtils.drawContinuousTexturedBox(buttonTextures, this.dropButtonX, this.dropButtonY, 0, 46 + k * 20, this.dropButtonWidth, this.dropButtonHeight, 200, 20, 2, 3, 2, 2, this.zLevel);
        
        String dropDownArrow = "v";
        if (isDroppedDown) {
            dropDownArrow = "^";
        }
        int arrowWidth = mc.fontRenderer.getStringWidth(dropDownArrow);
        mc.fontRenderer.drawString(dropDownArrow, this.dropButtonX + (this.dropButtonWidth / 2) - (arrowWidth / 2), this.dropButtonY + 3, 16777215);
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        int oldHeight = this.height;
        if (this.isDroppedDown) {
            this.height += 10 * listItems.size() + 4;
        }
        if (super.mousePressed(mc, mouseX, mouseY)) {
            if (this.isDroppedDown) {
                this.height = oldHeight;
            }
            this.isMouseDownOver = mouseOverDropDownButton(mouseX, mouseY);
            return true;
        } else {
            if (this.isDroppedDown) {
                this.height = oldHeight;
                this.isDroppedDown = false;
            }
            return false;
        }
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        if (this.isMouseDownOver && mouseOverDropDownButton(mouseX, mouseY)) {
            this.isDroppedDown = !this.isDroppedDown;
            this.isMouseDownOver = false;
        }
        if (hoverIndex != -1) {
            setListSelectedIndex(hoverIndex);
            this.isDroppedDown = false;
            if (this.callback != null) {
                this.callback.onDropDownListChanged(this);
            }
        }
    }
    
    public void clearList() {
        listItems.clear();
    }
    
    public void addListItem(String listItem) {
        listItems.add(listItem);
    }
    
    public String getListIndex(int index) {
        return this.listItems.get(index);
    }
    
    public int getListSelectedIndex() {
        return selectedIndex;
    }
    
    public void setListSelectedIndex(int index) {
        this.selectedIndex = index;
        this.displayString = listItems.get(this.selectedIndex);
    }
    
    public int getListSize() {
        return this.listItems.size();
    }
    
    public interface IDropDownListCallback {
        public void onDropDownListChanged(GuiDropDownList dropDownList);
    }
}
