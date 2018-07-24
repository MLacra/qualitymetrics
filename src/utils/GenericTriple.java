package utils;

public class GenericTriple<T1,T2,T3> {

	T1 first_element;
	T2 second_element;
	T3 third_element;
	
	public GenericTriple (T1 e1, T2 e2, T3 e3) {
		first_element = e1;
		second_element = e2;
		third_element = e3;
	}
	
	public T1 getFirst_element() {
		return first_element;
	}
	public void setFirst_element(T1 first_element) {
		this.first_element = first_element;
	}
	public T2 getSecond_element() {
		return second_element;
	}
	public void setSecond_element(T2 second_element) {
		this.second_element = second_element;
	}
	public T3 getThird_element() {
		return third_element;
	}
	public void setThird_element(T3 third_element) {
		this.third_element = third_element;
	}
	
	@Override
	public String toString() {
		return first_element+","+second_element+","+third_element;
	}
}
