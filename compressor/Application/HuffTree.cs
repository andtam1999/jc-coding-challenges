
public class HuffTree : IComparable<HuffTree>
{
    private readonly IHuffNode RootNode;

    public HuffTree(char element, int weight)
    {
        
        RootNode = new HuffLeafNode(element, weight);
    }

    public HuffTree(IHuffNode leftNode, IHuffNode rightNode, int weight)
    {
        RootNode = new HuffInternalNode(leftNode, rightNode, weight);
    }

    public IHuffNode GetRoot() { return RootNode; }

    public int GetWeight() { return RootNode.GetWeight(); }

    public int CompareTo(HuffTree? treeSubject)
    {
        if (treeSubject is null) { return -1; }

        int rootWeight = RootNode.GetWeight();
        int subjectWeight = treeSubject.GetWeight();

        if (rootWeight < subjectWeight) { return -1; }
        else if (rootWeight == subjectWeight) { return 0; }
        else { return 1; }
    }

    public static HuffTree BuildHuff(Dictionary<char, int> charFrequencies)
    {
        PriorityQueue<HuffTree, int> huffTree = new PriorityQueue<HuffTree, int>();
        foreach (char c in charFrequencies.Keys)
        {
            huffTree.Enqueue(new HuffTree(c, charFrequencies[c]), charFrequencies[c]);
        }
        return CombineHuffNodes(huffTree).Dequeue();
    }

    public static PriorityQueue<HuffTree, int> CombineHuffNodes(PriorityQueue<HuffTree, int> heap)
    {
        HuffTree temp1;
        HuffTree temp2;
        HuffTree temp3;

        while (heap.Count > 1)
        {
            temp1 = heap.Dequeue();
            temp2 = heap.Dequeue();
            temp3 = new HuffTree(temp1.GetRoot(), temp2.GetRoot(), temp1.GetWeight() + temp2.GetWeight());

            heap.Enqueue(temp3, temp3.GetWeight());
        }

        return heap;
    }
}