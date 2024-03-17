package cc.unitmesh.devti.language.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.util.ProcessingContext
import javax.swing.Icon

enum class BuiltinCommand(
    val agentName: String,
    val description: String,
    val icon: Icon,
    val hasCompletion: Boolean = false,
    val requireProps: Boolean = false,
) {
    FILE("file", "Read the content of a file", AllIcons.Actions.AddFile, true, true),
    REV("rev", "Read git change by file", AllIcons.Vcs.History, true, true),
    SYMBOL("symbol", "Read content by Java/Kotlin canonicalName", AllIcons.Actions.GroupBy, false),
    WRITE("write", "Write content to a file, /write:path/to/file:L1-L2", AllIcons.Actions.Edit, true, true),
    PATCH("patch", "Apply patch to a file, /patch:path/to/file", AllIcons.Vcs.Patch_file, false),
    RUN("run", "Run the content of a file", AllIcons.Actions.Execute, false),
    COMMIT("commit", "Commit the content of a file", AllIcons.Vcs.CommitNode, false)
    ;

    companion object {
        fun all(): List<BuiltinCommand> {
            return values().toList()
        }

        fun fromString(agentName: String): BuiltinCommand? {
            return values().find { it.agentName == agentName }
        }
    }
}

class BuiltinCommandProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        BuiltinCommand.all().forEach {
            result.addElement(
                LookupElementBuilder.create(it.agentName)
                    .withIcon(it.icon)
                    .withTypeText(it.description, true)
                    .withInsertHandler { context, _ ->
                        if (!it.hasCompletion) return@withInsertHandler

                        context.document.insertString(context.tailOffset, ":")
                        context.editor.caretModel.moveCaretRelatively(1, 0, false, false, false)

                        val editor = context.editor
                        AutoPopupController.getInstance(editor.project!!).scheduleAutoPopup(editor)
                    }
            )
        }
    }
}

