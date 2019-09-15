import javax.swing.JEditorPane
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeSelectionModel
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import java.awt.Dimension
import java.awt.GridLayout

case class NodePayload(val name: String, var detail: String = null) {
  if (detail == null) {
    detail = name
  }
  override def toString(): String = name
}


class TreeView(top: DefaultMutableTreeNode)
    extends JPanel(new GridLayout(1, 0)) with TreeSelectionListener {

  // Component hierarchy
  // JPanel
  //   JSSplitPane (splitPane)
  //     JScrollPane (treeView)
  //       JTree (tree)
  //     JScrollPane (htmlView)
  //       JEditorPane (detailPane)
  private var detailPane: JEditorPane = new JEditorPane()
  private var tree: JTree = new JTree(top)
  private var helpDetail: String = ""

  val treeView = new JScrollPane(tree)
  val htmlView = new JScrollPane(detailPane)

  val splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT)
  splitPane.setTopComponent(treeView)
  splitPane.setBottomComponent(htmlView)

  val myMinSize = new Dimension(100, 50)
  htmlView.setMinimumSize(myMinSize)
  treeView.setMinimumSize(myMinSize)

  splitPane.setDividerLocation(300)
  splitPane.setPreferredSize(new Dimension(500, 500))

  add(splitPane)

  tree.getSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION)
  tree.addTreeSelectionListener(this)
  detailPane.setEditable(false)
  expandAllNodes(tree)

  def valueChanged(e: TreeSelectionEvent) {
    val node = tree.getLastSelectedPathComponent.asInstanceOf[DefaultMutableTreeNode]
    if (node == null) return
    val nodeInfo = node.getUserObject
    val book = nodeInfo.asInstanceOf[NodePayload]
    displayDetail(book.detail)
  }

  private def displayDetail(detail: String) =
    detailPane.setText(if (detail != null) detail else "")

  private def expandAllNodes(tree: JTree) {
    var j = tree.getRowCount
    var i = 0
    while (i < j) {
      tree.expandRow(i)
      i += 1
      j = tree.getRowCount
    }
  }
}

object TreeView {
  def showGUI(top: DefaultMutableTreeNode, exitOnClose: Boolean = true) {
    val frame = new JFrame("TreeView")
    if (exitOnClose)
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.add(new TreeView(top))
    frame.pack()
    frame.setVisible(true)
  }

  def main(args: Array[String]) {
    showGUI( createNodes() )
  }

  def createNodes() = {
    val top = new DefaultMutableTreeNode(NodePayload("The Java Series"))

    var category: DefaultMutableTreeNode = null
    var node: DefaultMutableTreeNode = null

    category = new DefaultMutableTreeNode(NodePayload("Books for Java Programmers"))
    top.add(category)
    node = new DefaultMutableTreeNode(NodePayload("The Java Tutorial: A Short Course on the Basics",
      "tutorial.html"))
    category.add(node)
    node = new DefaultMutableTreeNode(NodePayload("The Java Tutorial Continued: The Rest of the JDK",
      "tutorialcont.html"))
    category.add(node)
    node = new DefaultMutableTreeNode(NodePayload("The JFC Swing Tutorial: A Guide to Constructing GUIs",
      "swingtutorial.html"))
    category.add(node)
    node = new DefaultMutableTreeNode(NodePayload("Effective Java Programming Language Guide", "bloch.html"))
    category.add(node)
    node = new DefaultMutableTreeNode(NodePayload("The Java Programming Language", "arnold.html"))
    category.add(node)
    node = new DefaultMutableTreeNode(NodePayload("The Java Developers Almanac", "chan.html"))
    category.add(node)

    category = new DefaultMutableTreeNode(NodePayload("Books for Java Implementers"))
    top.add(category)
    node = new DefaultMutableTreeNode(NodePayload("The Java Virtual Machine Specification", "vm.html"))
    category.add(node)
    node = new DefaultMutableTreeNode(NodePayload("The Java Language Specification", "jls.html"))
    category.add(node)
    top
  }
}
