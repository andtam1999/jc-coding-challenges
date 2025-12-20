public class HuffLeafNode : IHuffNode
{
    public char Element { get; }
    public int Weight { get; }

    public HuffLeafNode(char element, int weight)
    {
        Element = element;
        Weight = weight;
    }

    public bool IsLeaf() { return true; }
    public int GetWeight() { return Weight; }
}