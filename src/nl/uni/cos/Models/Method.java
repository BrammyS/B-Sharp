package nl.uni.cos.Models;

import java.util.List;

/**
 * A class representing a method in a BSharp.
 */
public record Method(String identifier, DataType type, List<DataType> args) {
}
