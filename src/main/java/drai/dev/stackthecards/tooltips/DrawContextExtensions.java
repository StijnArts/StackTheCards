package drai.dev.stackthecards.tooltips;

/**
 * Provides access to the {@link net.minecraft.client.gui.DrawContext} methods added by the mod.
 */
public interface DrawContextExtensions {
  void setTooltipTopYPosition(int topY);

  void setTooltipBottomYPosition(int bottomY);

  int getTooltipTopYPosition();

  int getTooltipBottomYPosition();

  void setMouseX(int mouseX);

  int getMouseX();

  void setMouseY(int mouseY);

  int getMouseY();
}
