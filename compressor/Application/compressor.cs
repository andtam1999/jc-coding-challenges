using System.Text;

namespace Application;

public class Compressor
{
    public static Dictionary<char, int> getCharFrequenciesIn(string filePath)
    {
        if (string.IsNullOrWhiteSpace(filePath))
            throw new ArgumentException("File path cannot be null or empty.");

        if (!File.Exists(filePath))
            throw new FileNotFoundException("File does not exist.");

        Dictionary<char, int> charFrequencies = new Dictionary<char, int>();
        using (FileStream fs = File.Open(filePath, FileMode.Open))
        {
            byte[] buffer = new byte[2048];
            UTF8Encoding encoder = new UTF8Encoding(true);
            int numBytesRead = 0;
            while ((numBytesRead = fs.Read(buffer, 0, buffer.Length)) > 0)  
            {  
                string content = encoder.GetString(buffer, 0, numBytesRead);
                foreach (char c in content)
                {
                    if (charFrequencies.ContainsKey(c))
                    {
                        charFrequencies[c]++;
                    }
                    else
                    {
                        charFrequencies[c] = 1;
                    }
                }
            }
        }
        return charFrequencies;
    }

}