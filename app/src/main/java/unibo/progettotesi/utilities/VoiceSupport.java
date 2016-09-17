package unibo.progettotesi.utilities;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;

public class VoiceSupport {

	public static boolean isTalkBackEnabled(Context context){
		AccessibilityManager am = (AccessibilityManager) context.getSystemService(context.ACCESSIBILITY_SERVICE);
		boolean isAccessibilityEnabled = am.isEnabled();
		boolean isExploreByTouchEnabled = am.isTouchExplorationEnabled();
		return isExploreByTouchEnabled;
	}
}
