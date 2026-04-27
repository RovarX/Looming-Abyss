package ui.customize

import arc.Core
import arc.scene.Element
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.ui.Label
import arc.scene.ui.ScrollPane
import arc.scene.ui.TextButton
import arc.scene.ui.TextField
import arc.scene.ui.layout.Table
import mindustry.ui.Styles

class TilesQueryDialog : FlowDialog("Tiles Query") {

    private val resultLabel: Label
    private val inputField: TextField
    private val resultPane: ScrollPane
    private val history = ArrayDeque<Pair<String, String>>()
    private val historyText = StringBuilder()

    init {
        cont.clear()
        cont.defaults().left().pad(4f)

        resultLabel = Label("")
        resultLabel.setWrap(true)

        val resultTable = Table().apply {
            add(resultLabel).width(320f).left().top().growY()
        }
        resultPane = ScrollPane(resultTable)
        resultPane.setScrollingDisabled(true, false)
        resultPane.addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Element?) {
                Core.scene.setScrollFocus(resultPane)
            }
        })

        cont.add(resultPane).height(140f).growX().left().top().row()

        inputField = cont.field("") { _ -> }
            .width(260f)
            .left()
            .get()

        val submit = TextButton("提交", Styles.defaultt)
        submit.clicked { submitQuery() }
        cont.add(submit).width(72f).height(42f).left()
    }

    fun submitQuery() {
        val expression = normalizeExpression(inputField.text)
        val value = TilesExpressionEvaluator.eval(expression, dialog.view.tiles)

        history.addLast(expression to value)
        historyText.append(expression).append('\n')
        historyText.append(value).append("\n\n")

        var trimmed = false
        while (history.size > 50) {
            history.removeFirst()
            trimmed = true
        }

        if (trimmed) {
            historyText.setLength(0)
            for ((query, result) in history) {
                historyText.append(query).append('\n')
                historyText.append(result).append("\n\n")
            }
        }

        resultLabel.setText(historyText.toString())
        resultLabel.invalidateHierarchy()
        resultPane.invalidateHierarchy()
        inputField.text = ""
    }

    private fun normalizeExpression(raw: String): String {

        var text = raw.trim()
        if (text.isEmpty()) return "tiles"

        while (text.startsWith("tiles.")) {
            text = text.removePrefix("tiles.")
        }

        if (text == "tiles") return "tiles"

        if (text.startsWith('.')) {
            text = text.removePrefix(".")
        }

        return if (text.isEmpty()) "tiles" else "tiles.$text"
    }
}
