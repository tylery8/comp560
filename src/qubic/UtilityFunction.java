package qubic;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UtilityFunction {
	
	private Map<SquareType, Double> _values;
	
	public UtilityFunction() {
		_values = new LinkedHashMap<SquareType, Double>();
		for (SquareType st : SquareType.values())
			_values.put(st, 0.0);
	}
	
	public double getValue(int plane, int line, int index) {
		return getValue(1L << (16*plane + 4*line + index));
	}
	
	public double getValue(long l) {
		return getValue(SquareType.valueOf(l));
	}
	
	public double getValue(SquareType st) {
		return _values.get(st);
	}
	
	public void setValue(int plane, int line, int index, double value) {
		setValue(1L << (16*plane + 4*line + index), value);
	}
	
	public void setValue(long l, double value) {
		setValue(SquareType.valueOf(l), value);
	}
	
	public void setValue(SquareType st, double value) {
		_values.put(st, value);
	}
	
	public void print() {
		for (Entry<SquareType, Double> entry : _values.entrySet())
			System.out.println(entry.getKey().toString().charAt(0) + entry.getKey().toString().toLowerCase().substring(1) + ": " + new BigDecimal(entry.getValue()).round(new MathContext(3)));
		System.out.println();
	}
}
