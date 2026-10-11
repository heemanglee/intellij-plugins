package io.heemang.gotorightsplit;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.ide.actions.OpenInRightSplitAction;
import com.intellij.ide.util.EditSourceUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtilCore;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;

/**
 * 커서(또는 마우스) 위치의 선언을 우측 에디터 분할 창에서 연다.
 *
 * <p>
 * 우측 분할 창이 이미 있으면 재사용하고, 없으면 새로 만든다. 따라서 좌측 창은 원래 코드를 유지하고 우측 창은 상세 내용을 보는 용도로 쓸 수 있다.
 */
public final class GotoDeclarationInRightSplitAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean inEditor = e.getProject() != null && e.getData(CommonDataKeys.EDITOR) != null;
        e.getPresentation().setEnabledAndVisible(inEditor);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            return;
        }

        PsiDocumentManager.getInstance(project).commitAllDocuments();
        int offset = offsetAt(editor, e.getInputEvent());
        TargetElementUtil util = TargetElementUtil.getInstance();
        PsiElement target = util.findTargetElement(editor, util.getAllAccepted(), offset);
        if (target == null) {
            HintManager.getInstance().showErrorHint(editor, "Cannot find declaration to go to");
            return;
        }

        // 라이브러리 클래스는 navigationElement가 소스 파일(첨부된 경우)을 가리킨다.
        PsiElement navigationElement = target.getNavigationElement();
        VirtualFile file = PsiUtilCore.getVirtualFile(navigationElement);
        Navigatable navigatable = EditSourceUtil.getDescriptor(navigationElement);
        if (file == null || navigatable == null) {
            HintManager.getInstance().showErrorHint(editor, "Cannot navigate to declaration");
            return;
        }

        OpenInRightSplitAction.Companion.openInRightSplit(project, file, navigatable, true);
    }

    /** 마우스로 실행됐다면 클릭한 위치를, 아니면 캐럿 위치를 기준으로 삼는다. */
    private static int offsetAt(Editor editor, InputEvent event) {
        if (event instanceof MouseEvent mouseEvent && mouseEvent.getComponent() != null) {
            Point point = SwingUtilities.convertPoint(mouseEvent.getComponent(),
                    mouseEvent.getPoint(), editor.getContentComponent());
            return editor.logicalPositionToOffset(editor.xyToLogicalPosition(point));
        }
        return editor.getCaretModel().getOffset();
    }
}
