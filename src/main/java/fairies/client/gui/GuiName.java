package fairies.client.gui;

import org.lwjgl.input.Keyboard;

import fairies.FairyFactions;
import fairies.entity.EntityFairy;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiName extends GuiScreen
{
    protected String screenTitle;
    private EntityFairy fairy;
    private String nameText;

    public GuiName(EntityFairy entityfairy)
    {
		FairyFactions.LOGGER.info("GuiName: constructed");

        screenTitle = "Enter custom name or leave blank for default:";
        fairy = entityfairy;
        nameText = "";

        if (fairy != null && fairy.getCustomName() != null)
        {
            nameText = fairy.getCustomName();
        }
    }

    @Override
    public void initGui()
    {
        buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 120, "Done"));
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        if (fairy != null)
        {
    		FairyFactions.LOGGER.info("GuiName.onGuiClosed: isRemote = "+fairy.world.isRemote);

            if (fairy.world.isRemote)
            {
                //String s1 = "setfryname " + fairy.getEntityId() + " " + nameText;
                FairyFactions.proxy.sendFairyRename(fairy, nameText);
            }
            else
            {
                fairy.setCustomName(nameText);
            }
        }
    }

    @Override
    public void updateScreen()
    {
        if (fairy == null || fairy.isDead)
        {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        if (!guibutton.enabled)
        {
            return;
        }

        if (guibutton.id == 0)
        {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        if (i == 28)
        {
            mc.displayGuiScreen(null);
        }

        if (i == 14 && nameText.length() > 0)
        {
            nameText = nameText.substring(0, nameText.length() - 1);
        }

        if (ChatAllowedCharacters.isAllowedCharacter(c) && nameText.length() < 12)
        {
            nameText += c;
        }
    }

    @Override
    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        drawCenteredString(	fontRenderer, screenTitle, width / 2, 40, 0xffffff);
        drawCenteredString(	fontRenderer, nameText, width / 2, 56, 0xffffff);
        super.drawScreen(i, j, f);
    }

}
