package io.heemang.gotorightsplit;

import com.intellij.openapi.actionSystem.ActionPromoter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataContext;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Cmd+Option+Click은 기본 키맵에서 Go to Implementation에도 할당되어 있다. 두 액션이 경쟁할 때 이 플러그인의 액션이 먼저
 * 실행되도록 순서를 앞당긴다.
 */
public final class RightSplitActionPromoter implements ActionPromoter {

    @Override
    public List<AnAction> promote(@NotNull List<? extends AnAction> actions,
            @NotNull DataContext context) {
        return actions.stream()
                .filter(GotoDeclarationInRightSplitAction.class::isInstance)
                .map(AnAction.class::cast)
                .toList();
    }
}
