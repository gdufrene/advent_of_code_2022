package advent2022;

import java.util.List;


public class Day06 implements AdventDay {

    public static void main(String[] args) throws Exception {
        
        String str = "pwjwljjjvqjqjzqjqvvpgvpggmdgdrrzmzfzmzffzbbjnbjnjddsdpsdsgsgvsgvgmmzvvpspvspsdsbsbffhwhlwwzllftfhhrfrsrrnnngqgtglgfgtffnssrspsqppdvdtdwwpfpzffnpnddvsswjswjjhdjdrjrwwfpwfpfjppzwzvzpzrznzgzcgzgpgfpffwggtbbhsslzztpplltdtqtctrrszrzszpsprptrtgrtrzrvzrvzrrsfsbswsvvbdbjjgljjwqwgqwwtmwwnbbzdbdwwsnntztstbssnjssjmmldmmrrnfrfgfbbszzzlrrwzwtwwbrwrlwrwzwmwqmwwtqwwtvvdqdtqqfnfjnjsnslltcllvmmdmttvsszrsrwssdmdttdmdrmrsrvrllrhlltmlmlzmlzzhvhbbflfsslclpcczmmcjmjccpgprrgnrrzqqmgqggfvgffbsfsbfbttrgtrtptgtjtnnfbfmmrrcmcwwbbmwmvvvtppslsvvlsvswwztwztzqqpggdccmlldlplggrgddlrddvwvsswllsffbllsbbslshslswsllnqlqrqqpmqmtmqtmqtmmbwmbbzdbzbttvptvpvgpvggblbsspgppvmvsswfssfdfrdffpbfpprmrssmbbmggszslzlslblsldsldssjjsrjjjdfdjdtdmdnnncddpfddrjdjvjfvjffftmfmqqqqqfjjhssbzbbfjjmmzgzllphlplhlplfpfnpnsppjjqsshpssjdjzzwvvjfvfnfttmtbbgtgbbdpbbjppzcpzzvcvpvvdcvvcfvvfsvsccmhhrwrjjsrjrvrfvfwvvtftzztlztlthlhrhnnlpnllnmnfmffzpffhlhbhqbhqhfhwwlglnndgdwdswwgtwgtgsttpbprpqpnnbsnbsnszzrszsqzqlqggvhghrrvprvvvqnnmmrwrbrrmttwrrlmrrrddmsddbhdhdjhdhrhffppphghvvtztptjjlppcqcscjscstsmsbspsgpgqgmmndmdvvpnpnrrjtrthhtzzjlzzrwzrrfsfflrrmffhlhglhhhhvwwttlcttbqqzzbzzzbhbsscqqjggqpggqffbttrfrjjjqmmpttlvvqlvqqqtgtrtcrcbcnbbhfbfhbhqbhqbqvbvqvrqvvgttqwqpwqwnnpgngpphjppztptnnrssjqqrplzrvmwmbrbgbnggvzpmphqsrjrdhtslpmmwrhgcndwtbsbrfmsplzqswsnmrwdwwhzmpbqcmjfsmnwqnjmvdczhgmtfjwnjfdllfzdwpwgclpbdqtqnqqqvpthltznfzshhgrwwqclpplmdwtpjszrdwwzfbljcjmqmhptfhvcbvgfjfftbsfglwqldphdzzgcmvtsbhlsdncfjcsqrqrtdhttcwzlbqhvgppbrjfzdzwzprwpfflmdspcmqcbdhsvwjswwbzwnqrshbqfnmtdzrsrjgqngntllcgwjnmjqvtgwvttfqrcjlhbpcrszlngfmdgzprcdttgbjpcdzbhtdghpltcbvcddnslhqthfvzjtspqlzhdprhgtrlqqgtsqwwjqthgdwfgdfzhrnrwlrpqmgqltgldpjqgjzvngrphclbfftnwfnfvsvhftthptqfnvlftdpdhcrjdhfwtpwwvsblgntdwcpnsprhnpjtjsprdrdjwlhnmnzmmjmcdfsctzgmlqwwrwztjndqgpqrvdgplcnntqhfjlzjszpdwnvlwdzzgpzvplglgrmsjgjpmsrdsgzlfblgbgszgdtgsggqvhzmnfcnvlnzrfpqphctlcqccqzslmlsbbztnpncqpgscgdmmsgfwqrzpmbqrmfqsnnggswhmgmtmgdmhwthbgbdsrtnsrvdfdhlhhczgdsdqnpsgjzpbnsmgrvsfdjlhgjfjjwqnrnrbzdzcjlstclpqfnrflgnzbdbzvbjcbgqrrrlfcpgptcghhqqfsvsgljvjhdgdgcjtnrqsctmwhzbmbrfrsvndhfrtwlfgvqcbjsvttrctfshrggdvgbhthnwbwqglrvfbsqnbhdwgzhbccjnlhtcbjlpgrvttthnwvvspnzvqhjvmtwshcstdjhfqhqcgvwqwwwwrfdmnjhldsrhgmtjddsghdmdrpczbcjflmbhszctmvdttfrrqqpwslhvqbjlsrjdjrcqjrhwgjlsqmvdpvvvlbzwtthptpsggddcbqbhrvrpdtncvgndclhpngzgfqdqgwwbrjltjtqbpbtbzjmfmnjnlqmtzvdqdwqhbgptplrgdsfpjzfrpdcdsznwffpnzsmpqjfbcpddqjgfqhqbwsfmzgstfdnzhphhvgbzvjrlmqmrznpctftcmbtdpbfbfpfpjtjhbcrrssnlvtrtnzvwtjwplclpqndpfstjmghmzsllhntprtjwlppjnjgjzlvlcbcwjvrjqjhfnnpmwlpngwtvvbcllmjzqfrwvtvsrvbcpfwcdfwmdwvztwtbrgvlvmfjmpzdzmfbcmrsqfwqjfjfrgmnblnzfzcvgwllvmqfmdlqqgvvjrptlrjcwphvchwhmtwhnlnjprqhlrhmdfptvpshjbzrhptvnqfvjfjcnglnbfhbwghqqjbqzdthjwqzznwfsmqmbsqnwrdrrwjgzjdmgtsqswlqcpshdmcfjttpszqmsjhgfsrvgchgwzbqgbdqhmbndmnmwjsnjjvmtpprbtlwzpvfdnbtjnzzvlwndgbhgwbpllvfghwvwjmlpnzfjzjwwmtvbbfndppbqwhjlwgtswmgffddbhnwqljvgcvfnqmzgvfmjwsbcrpgtcpchlblccgpgpmddsjsfwbnvnsfttnsqjshchdztvsbjwsfmszfwpwsmgzvvcfddtczvvmnhgjffrsfqzfmphpfblmwgcbbrjqzdztzzzjhqmjzrgmwgrqdfqdbjsgwfndqgnmdvjlwdtjpjtpcqlhtcfvnmzjswldrbqjpmrlqwhvnqjshbqqvzwwsdjmspgbvrgvpjjnwsvnvnppvlnqbdllfczjjftpnlrjfvcwgwbdmldtcnczqzcptjjrgglnnbgmrdffgmnnwvjzwbgcncnhzmthswrdsrhchprrrrnhjfnzmsgfjltqzmttvhslnsgcjfgqwcsddfstcstspcpdbznvdrnqhwqsfgqtdbtwspfswfjbzgtqjpvzfhfdblszblmgrmmlwvnwwdsdjjvrsfjfjltcsfccplftvpltqshgnpnqlqcglrhvzldptspnbvjcchnnvzvbbqnnnbnggrhpcgqtgnjdqplswtblgtwqzmltjjhpdttgbcvhfrsdcgjzswvtbbhrpnzmrjhgznbdpqgqdhwcnmgflpdtbzdbvzvslbvvwdpcnwjtvjhgncnljfwlrvqgdrjhdcprsqjrmwwlcrrvsjtlmqmjtcbqwcbmgnvfshdgmmfffzvwjphjfspvdzjsqdlgqfdjwwshdcssqvvgdcvvtmwlfjdvtfllrvltmrsgpdtdqsfjpcvjnqszpnbqqlnpdvhtswbgwnpcqpgzqwlgsmlnlngcdmqhchcdgfmrhfwwrgrrdrhcsbbcrhghdjrcsltchqghvmbvbbpqzqbgwmqrgwchhbvdsqbqrfcbzwjrlqtnmghtjbtjdpngcjzswfmjfphjnftbhdgvwjsvqfbsfgqhfbcrgrsppsvbnpwlhsdrffcmmgzpjfsvllcrbtwrwddthfjvjndzfzbcmglhbzpzwwbtzdpdlwrnbzqjbqwpbdlwfddbtzjhqshmcghqfcrzrmrtmqwpqhvqzbfwhbssgjcmzqcpvnntbpfqwhbmtjdtbtrrdhsvzqjltdshtlvwwmlbdzlvjhmtppnbqcjnncpslcggsjbrmzvdgqzclwszgzfqthndnjfjrznlmmtjwwhnzvhnjncccpczrftvhtdhjbzvwvlgqhdnfqdqrhctfffpcnqzdrgqqzcczdjvpzqfgfcpjzqhbwshsqhvqzpsb";
        // String str = "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg";
        
        System.out.println("Part1 -> " +
            new Day06()
            .startOfPacket(str)
        );
        System.out.println( "Part2 -> " +
            new Day06()
            .startOfMessage(str)
        );
    }
    
    List<String> lines;
    int nbLines;
    
    
    int startOfPacket(String str) {
        byte[] arr = str.getBytes();
        int i;
        for (i = 0; i < arr.length; i++) {
            if (   arr[i] != arr[i + 1] 
                && arr[i] != arr[i + 2] 
                && arr[i] != arr[i + 3] 
                && arr[i + 1] != arr[i + 2]
                && arr[i + 1] != arr[i + 3] 
                && arr[i + 2] != arr[i + 3] 
            ) break;
        }
        
        return i+4;
    }
    
    int startOfMessage(String str) {
        int len = str.length();
        byte[] arr = str.getBytes();
        int i = 0;
        outter:
        for ( i = 0; i < len; i++ ) {
            int to = i + 14;
            if ( to > len ) to = len;
            for ( int j = i;  j < to; j++ ) {
                for ( int k = j+1;  k < to; k++ ) {
                    if ( arr[j] == arr[k] ) {
                        //found = j;
                        //System.out.println(((char) arr[j]) +" is at " +j+" and "+k);
                        i = j;
                        continue outter;
                    }
                }
            }
            return i+14;
        }
        return -1;
    }
    
}
