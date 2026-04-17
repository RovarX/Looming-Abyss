package test;

import arc.util.Time;
import mindustry.ui.dialogs.BaseDialog;

/**
 * 用于在游戏中显示一个测试对话框。
 */
public class TestDialog {

	public static TestDialog TD = new TestDialog();
	
	public static void show() {
		Time.runTask(0f, () -> {
			BaseDialog dialog = new BaseDialog("Test Dialog");
			dialog.cont.add("This is a test dialog from TestDialog.").row();
			dialog.cont.button("Close", dialog::hide).size(140f, 48f);
			dialog.show();
		});
	}

}
 