package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;


public class BitwiseDataHelper {
    //public static String parseByteToBinary(byte value){return parseByteToBinary(value, true);}
    //We don't need a space8 option here because bytes are only 8 bits long...
    public static String parseByteToBinary(byte value){
        String parse = Long.toBinaryString(Byte.toUnsignedLong(value));
        int length = parse.length();
        StringBuilder builder = new StringBuilder();
        while (length++ < 8) builder.append("0");
        return builder.append(parse).toString();
    }
    public static String parseIntToBinary(int value){return parseIntToBinary(value, true);}
    public static String parseIntToBinary(int value, boolean space8){
        String parse = Long.toBinaryString(Integer.toUnsignedLong(value));
        int length = parse.length();
        StringBuilder builder = new StringBuilder();
        while (length++ < 32) builder.append("0");
        builder.append(parse);
        if (space8) {
            //subtract 9 because length is +1 larger than the string length,
            // and we don't want to append " " to the very last string index because
            // that would leave one dangling whitespace
            length -= 9;
            for (; length > 0; length -= 8) builder.insert(length, " ");
        }
        return builder.toString();
    }
    public static String parseLongToBinary(long value){return parseLongToBinary(value, true);}
    public static String parseLongToBinary(long value, boolean space8){
        String parse = Long.toBinaryString(value);
        int length = parse.length();
        StringBuilder builder = new StringBuilder();
        while (length++ < 64) builder.append("0");
        builder.append(parse);
        if (space8){
            //subtract 9 because length is +1 larger than the string length,
            // and we don't want to append " " to the very last string index because
            // that would leave one dangling whitespace
            length -= 9;
            for (; length > 0; length -= 8) builder.insert(length, " ");
        }
        return builder.toString();
    }

    /**Parses an array of bytes into a legible binary format, similar to
     * {@link java.util.Arrays#toString(byte[])} but formats each entry via
     * {@link BitwiseDataHelper#parseByteToBinary(byte)}
     * @param a an array of bytes to parse into binary strings
     * @return a string of all entries from the argument formatted between '[]' with
     * each entry parsed into binary according to {@link BitwiseDataHelper#parseByteToBinary(byte)}
     * separated by a comma and space
     */
    public static String parseByteArrayToBinary(byte[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(parseByteToBinary(a[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }
    /**Parses an array of integers into a legible binary format, similar to
     * {@link java.util.Arrays#toString(int[])} but formats each entry via
     * {@link BitwiseDataHelper#parseIntToBinary(int)}
     * @param a an array of integers to parse into binary strings
     * @return a string of all entries from the argument formatted between '[]' with
     * each entry parsed into binary according to {@link BitwiseDataHelper#parseIntToBinary(int)}
     * separated by a comma and space
     */
    public static String parseIntArrayToBinary(int[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(parseIntToBinary(a[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }
    /**Parses an array of longs into a legible binary format, similar to
     * {@link java.util.Arrays#toString(long[])} but formats each entry via
     * {@link BitwiseDataHelper#parseLongToBinary(long)}
     * @param a an array of long to parse into binary strings
     * @return a string of all entries from the argument formatted between '[]' with
     * each entry parsed into binary according to {@link BitwiseDataHelper#parseLongToBinary(long)}
     * separated by a comma and space
     */
    public static String parseLongArrayToBinary(long[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(parseLongToBinary(a[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    /**
     * Sets the bit of a "word" (long) at the given offset to whatever the bit argument is
     * <p>This method is a bit needlessly performance-hungry for how little it does. Sucks, but it also isn't... THAT unoptimized...</p>
     * @param word the "word" (long) to write to (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bit to modify. Index 0 is the first bit farthest to the right.
     * @param bit the bit (in the form of a boolean) to write. 0 or 1
     * @return A new "word" that is identical to the "word" argument except for the bit at the supplied index being changed to the supplied bit argument.
     * May be fully identical if the original bit was the same as the argument
     */
    public static long writeBit(final long word, final int bitOffset, final boolean bit){
        return bit ? bit1AtPosition(word, bitOffset) : bit0AtPosition(word, bitOffset);
    }
    /**
     * Sets the bit at the supplied index of the "word" (long) to 1, regardless of the original value
     * @param word the "word" (long) to write to (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bit to modify. Index 0 is the first bit farthest to the right.
     * @return A new "word" that is identical to the "word" argument except for the bit at the supplied index being changed to 1.
     * May be fully identical if the original bit was the same as the argument
     */
    public static long bit1AtPosition(final long word, final int bitOffset){
        //Create a mask of [00...01], shift by the offset, then "Or" it against the word. return
        return word | (1L << bitOffset);
    }
    /**
     * Sets the bit at the supplied index of the "word" (long) to 0, regardless of the original value
     * @param word the "word" (long) to write to (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bit to modify. Index 0 is the first bit farthest to the right.
     * @return A new "word" that is identical to the "word" argument except for the bit at the supplied index being changed to 0.
     * May be fully identical if the original bit was the same as the argument
     */
    public static long bit0AtPosition(final long word, final int bitOffset){
        //Create a bitmask with the desired bit set to 1
        // then NOT to change it to a bitmask of 1's with the desired bit being 0
        // finally, AND the mask with the word, flipping the desired bit to 0
        return word & ~(1L << bitOffset);
    }
    /**
     * Writes a list of bits to the supplied word then returns the result. Does NOT modify any of the variables, just returns a new "word" (long)
     * <p>The "ink" must have all of its relevant bits secluded to the first bits of the word within the inkRange as defined by the last argument.
     * The rest of the unrelated bits in the word need to be 0</p>
     * @param word the "word" (long) to write to (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bit to modify. Index 0 is the first bit farthest to the right.
     * @param ink the "word" containing all the bits to write to the original word. All relevant bits must start
     *           from index 0 out to the supplied ink range-- all other bits MUST BE 0, otherwise corruption may occur.
     * @param inkRange how many bits to expect to write from the ink. Do NOT add more bits to the ink than allocated by the inkRange.
     *                It can corrupt bits outside the range otherwise.
     * @return A new "word" that is identical to the "word" argument except for the bits at the supplied index being changed
     * to the bits supplied in the ink. May be fully identical if the original bits were the same as the argument
     */
    public static long writeRange(final long word, final int bitOffset, final long ink, final int inkRange){
        //Define a mask of all 1's, bitshift to the right by the inkRange, then NOT.
        // Then shift the mask over by the bitOffset then NOT again so all the bits will be 1's
        // excluding the 0's covering the area where the wanted bits are
        long mask = ~(-1L << inkRange) << bitOffset;
        //Mask all the wanted bits in the word to 0 by AND'ing the mask onto the word
        //Example: starting with bits [10111011], and we want to mask 4 bits that are offset by 1.
        // We create a mask of [11100001], when AND'ed against the word, it will clear all bits
        // within the zeroes. Result would be [10100001] (note the block of 4 zeroes)
        long wordMasked = word & mask;
        return wordMasked | (ink << bitOffset); //Finally, "Or" in the ink (bits to write)
        //All of this could be compressed into 1 line but that makes it REALLY hard to read, so... prob wont
    }

    /**
     * Writes the bits in the supplied "ink" to the sentence at the supplied index, taking care of overflow when needed
     * <p>Surface-level debugging has been made on this method, however it has yet to be properly used;
     * bugs MAY appear and data MAY be corrupted if this method is not actually properly programmed... apologies in advance --Pilot</p>
     * @param sentence the original set of bits to write the ink to. Make SURE that all the bits that will be written
     *                (defined by both the ink and the inkRange argument) will fit within this array, otherwise an exception will be thrown
     *                 (see {@code Throws})
     * @param bitOffset the amount of bits between the first bit index of the first word in the sentence and the location to write the new bits to.
     *                  The offset is in context to the sentence argument-- if the value is greater than 64 (the bitsize of a long)
     *                  it will skip the first word within the sentence argument, etc.
     * @param ink an array of words (longs) with bits to write to the sentence
     * @param inkRange how many bits from the ink to write to the sentence. Any bits (defined or otherwise) within the ink argument
     *                 but outside the bounds defined will be ignored
     * @return a modified version of the sentence long array argument with the new bits written to the supplied index range
     * @throws InvalidBitWriteOperation if the supplied inkRange argument extends beyond the scope of either of the supplied long arrays
     * (argument {@code sentence} or {@code ink})
     */
    public static long[] mergeBitSentences(final long[] sentence, final int bitOffset, final long[] ink, final int inkRange)
            throws InvalidBitWriteOperation {
        //Indexes for both the sentence and the ink-- second index is only needed in the loop but is defined here anyway
        int sentenceIndex = Math.floorDiv(bitOffset, 64),
                inkIndex = 0;
        //The bitOffset could be greater than 64 if the first word in the sentence is not the first one we want to write to
        int localOffset = bitOffset % 64;
        //First serves as a holder for the inverse of the bitOffset, then keeping track of the amount of bits to write
        int inkTracker = 64 - localOffset;
        int bitsToWrite = Math.min(inkTracker, inkRange); //How many bits to write in this invoke
        //Write the ink's first word with all the bits that could fit in the first sentence word isolated to the start for merging
        sentence[sentenceIndex] = writeRange(sentence[sentenceIndex], localOffset, isolateRange(ink[0], 0, bitsToWrite), bitsToWrite);
        inkTracker = inkRange - inkTracker;

        //Now, write all the bits to the proceeding words until we are done!
        while (inkTracker > 0){
            //Merge the rest of the bits of the last word in the ink that was written down
            // with as many of the bits of the next word that can fit
            long toWrite = writeRange(
                    //The "old" word (one from the last loop), with all "unused" bits now isolated to the beginning
                    isolateRange(ink[inkIndex], bitsToWrite, 64 - bitsToWrite),
                    //The offset, how many of the "old" bits in the old word to not rewrite... (all the bits left unused in the prior loop)
                    bitsToWrite,
                    //The next word in the ink to grab bits from, with all the wanted bits isolated--
                    // totaling the first bit of the word to the last bit that can fit,
                    // E.G. either the rest of the bits to write or the amount of bits NOT written
                    // in the last iteration, whichever is smaller
                    // (seen in [bitsToWrite = Math.min(inkTracker, 64 - bitsToWrite)])
                    //bitsToWrite is reassigned to have the amount of bits taken from the "new" word
                    // (for use in the next loop iteration)
                    isolateRange(ink[inkIndex + 1], 0,
                            (bitsToWrite = Math.min(inkTracker, 64 - bitsToWrite))),
                    //Finally, the amount of bits to write, which has been calculated
                    // and assigned in the prior argument.
                    //This value will be remembered for the next loop
                    bitsToWrite);
            //If the supplied ink (or inkRange) includes more information than the sentence can hold...
            if (++sentenceIndex >= sentence.length || ++inkIndex >= ink.length){
                //...throw an error, that's no good and shouldn't happen :/
                throw InvalidBitWriteOperation.invalidParagraphWrite(inkRange, bitOffset, sentence.length);
            }
            //Write all the bits we want to the next word in the sentence.
            // (local) bitOffset is always 0 for the given word
            //The "range" is the remainder of inkTracker div 65
            // (NOT 64 because 64 as a remainder is a perfectly valid value for writing)
            sentence[sentenceIndex] = writeRange(sentence[sentenceIndex], 0, toWrite, inkTracker % 65);
            inkTracker -= 64;
        }
        return sentence;
    }
    /**
     * Writes the bits in the supplied "ink" to the sentence at the supplied index, taking care of overflow when needed
     * <p>Simplified version of {@link BitwiseDataHelper#mergeBitSentences(long[], int, long[], int)} that takes just a long argument for the {@code ink}
     * argument rather than an array for better performance</p>
     * @param sentence the original set of bits to write the ink to. Make SURE that all the bits that will be written
     *                 (defined by both the ink and the inkRange argument) will fit within this array, otherwise an exception will be thrown
     *                 (see {@code Throws})
     * @param bitOffset the amount of bits between the first bit index of the first word in the sentence and the location to write the new bits to.
     *                  The offset is in context to the sentence argument-- if the value is greater than 64 (the bitsize of a long)
     *                  it will skip the first word within the sentence argument, etc.
     * @param ink the bits to write to the sentence
     * @param inkRange how many bits from the ink to write to the sentence. Any bits (defined or otherwise) within the ink argument
     *                but outside the bounds defined will be ignored
     * @return a modified version of the sentence long array argument with the new bits written to the supplied index range
     * @throws InvalidBitWriteOperation if the supplied inkRange argument extends beyond the scope of either the sentence or the ink
     * (argument {@code sentence} or {@code ink})
     */
    public static long[] writeRangeToSentence(final long[] sentence, final int bitOffset, final long ink, final int inkRange)
            throws InvalidBitWriteOperation {
        //If the inkRange extends beyond the scope of the supplied ink
        if (inkRange > 64) throw InvalidBitWriteOperation.rangeOutOfBounds(inkRange, 64);

        int sentenceIndex = Math.floorDiv(bitOffset, 64); //Index for the sentence
        //The bitOffset could be greater than 64 if the first word in the sentence is not the first one we want to write to
        int localOffset = bitOffset % 64;
        //The amount of bits to write
        //[Math.min(inkRange, 64 - localOffset)] ensures that it writes either ALL the bits if it will fit, or just all bits it can fit
        int bitsToWrite = Math.min(inkRange, 64 - localOffset);
        //Writes as many bits as it can from the ink to the first valid word in the sentence
        //[(ink << localOffset) >>> localOffset] removes all trailing bits that do NOT need to be written to the first word
        //writeRange(args...) requires the ink argument to have all values to be written pushed to the start and everything else to be defaulted to 0
        sentence[sentenceIndex] = writeRange(sentence[sentenceIndex], localOffset, (ink << localOffset) >>> localOffset, bitsToWrite);
        //If there is still more we need to write, write the rest to the next word
        int leftToWrite = inkRange - bitsToWrite;
        if (leftToWrite > 0){
            long iso = isolateRange(ink, bitsToWrite, leftToWrite);
            sentence[++sentenceIndex] = writeRange(sentence[sentenceIndex], 0, iso, leftToWrite);
            //sentence[++sentenceIndex] = writeRange(sentence[sentenceIndex], 0, isolateRange(ink, bitsToWrite, leftToWrite), leftToWrite);
            //Commented out code is a compressed variant of the above, iirc the iso was assigned
            // to a local variable for debugging and testing and I forgor to change back
            // idrc tho they both work fine, the current is just a bit more legible
        }
        return sentence;
    }

    /**
     * Locates a range of bits within the "word" then isolates it, returning a word with all the wanted bits
     * pushed to the first indexes with everything else defaulted to 0
     * @param word the "word" (long) to read from (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bits to read. Index 0 is the first bit farthest to the right.
     * @param range how many bits to read, starting from the index outwards. Can NOT be greater than or equal to 64 (the amount of bits within a long)
     * @return a new "word" with all the wanted bits (defined by the bitOffset and range) isolated to the starting index outwards.
     * All other bits default to zero
     */
    public static long isolateRange(final long word, final int bitOffset, final int range){
        //Shift the word over by the offset to shave off all irrelevant bits
        // in front of the wanted ones (seen in [word >>> bitOffset])
        //Create a mask of full 1's then bitshift in as many zeros as there are bits to save
        // (seen in [-1L << range]) and NOT (~) to make it an AND mask
        // then use AND bitmasking to preserve the portion we want while defaulting everything else to 0
        //Example with bits[10110100], we want the first 4 [range = 4],
        // so we make a bitmask of [11110000] via a full mask of 1's [11111111 = -1]
        // then shifting over left by the range (4)
        // NOT the mask to [00001111] then AND the mask into the word to get [00000100]
        // ---
        //Note that above example assumes a bitOffset of 0, if the above value had a bitOffset of 2
        // then the bits would be shifted right, equating [00101101] before masking.
        //Scaled down model, longs are comprised of 64 bits, not 8
        return (word >>> bitOffset) & ~(-1L << range);
    }

    /**
     * Attempts to locate and then isolate a list of bits that extends between two words
     * @param word1 the first word, MUST BE THE WORD DIRECTLY BEFORE THE SECOND ARGUMENT within the sentence that the words are retrieved from.
     *              Failure to comply will result in junk data
     * @param word2 the second word, MUST BE THE WORD DIRECTLY AFTER THE FIRST ARGUMENT within the sentence that the words are retrieved from.
     *              Failure to comply will result in junk data
     * @param bitOffset the "offset" (index) of the first bit to read. Must be less than 64
     * @param range how many bits to read. This plus the bitOffset should equal >64,
     *             otherwise the method will assume the bits are all located within the first argument and just isolate from there
     * @return a new "word" with all the wanted bits isolated to the start, everything else defaulting to 0
     */
    public static long isolateAndMergeAcrossWords(long word1, long word2, int bitOffset, int range){
        int bitEnd = bitOffset + range; //The bit index of the last bit to locate
        //If the last index is within the first word, just use the normal isolate method
        if (bitEnd < 64) return isolateRange(word1, bitOffset, range);
        else{
            int bitsInFirst = 64 - bitOffset; //Find how many bits are located in the first word
            //Isolate the first bits in the first word
            long iso = isolateRange(word1, bitOffset, bitsInFirst);
            //Then isolate the ones in the second word and shift them over to accommodate
            // the first set of bits and then "OR" the results to merge
            return iso | isolateRange(word2, 0, range - bitsInFirst) << bitsInFirst;
        }
    }

    /**
     * Returns a boolean identical to the bit at the given index of the word.
     * @param word the "word" (long) to read from (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bit to read. Index 0 is the first bit farthest to the right.
     * @return {@code true} if the bit at the index was 1, {@code false} if the bit was 0
     */
    public static boolean readBitAt(final long word, final int bitOffset){
        //Shift ALL bits over to the right by the offset
        // (including the sign bit, hence ">>>" and not ">>") so the wanted bit is the first bit
        // Then, mask out all other bits by setting them to zero [... & 1L]
        //If the first bit is one [00...01 = 1] return true
        // else it is [00...00 = 0] which is zero, meaning the bit to test was 0.
        return ((word >>> bitOffset) & 1L) == 1;
    }

    public static class InvalidBitWriteOperation extends Exception{
        public static InvalidBitWriteOperation invalidParagraphWrite(int inkRange, int bitOffset, int paragraphSize){
            return new InvalidBitWriteOperation("ERROR! An invalid attempt to write words to a paragraph was preformed! Attempted to write [" +
                    inkRange +
                    "] bits offset by [" +
                    bitOffset +
                    "] bits to a paragraph of [" +
                    paragraphSize +
                    " (" +
                    paragraphSize * 64 +
                    " bits!)] words long!");
        }
        public static InvalidBitWriteOperation rangeOutOfBounds(int inkRange, int expectedMaxRange){
            return new InvalidBitWriteOperation("ERROR! An invalid attempt to write words to a paragraph was preformed! The supplied inkRange [" +
                    inkRange + "] values extended beyond the maximum range of the supplied ink. Max Range: ["
                    + expectedMaxRange + "]");
        }
        public InvalidBitWriteOperation(String msg){super(msg);}
    }
}
