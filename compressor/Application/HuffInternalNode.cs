public class HuffInternalNode : IHuffNode
{
    public IHuffNode LeftNode { get; }
    public IHuffNode RightNode { get; }
    public int Weight { get; }

    public HuffInternalNode(IHuffNode leftNode, IHuffNode rightNode, int weight)
    {
        LeftNode = leftNode;
        RightNode = rightNode;
        Weight = weight;
    }

    public bool IsLeaf() { return false; }
    public int GetWeight() { return Weight; }
}