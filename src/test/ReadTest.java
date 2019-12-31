package test;
import init.ReadFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import beans.*;


public class ReadTest {
	
	private static  ArrayList<Task>tasks;
	private static ArrayList<Employee>employees;
	private static ArrayList<Arc>arcs;
	private static int [] employeeNumber_Task;
	
	public static void main(String [] args){
		ReadFile readFile=new ReadFile("D:\\windows","1.txt");
		readFile.init();
		readFile.sort();
		tasks=readFile.getTasks();
		employees=readFile.getEmployees();
		readFile.getEmployee_Task();
		employeeNumber_Task=readFile.getEmployeeNumber_Task();
		arcs=readFile.getArcs();
	
		/*for(int i:employeeNumber_Task){
			System.out.print(i);
		}
		System.out.println("");
		
		for(int i=0;i<tasks.size();i++){
			System.out.print(i+":");
			for(Employee employee:tasks.get(i).getEmployees()){
				System.out.print(employee.getNumber()+"\t");
			}
			System.out.print("��"+tasks.get(i).getEmployees().size()+"��");
			System.out.println("");
		}
		*/
		
		
		System.out.println("��֤�����Ƿ��Ѿ�ȫ������ȷ�洢��");
		for(Task task:tasks){
			System.out.println("����"+task.getNumber()+"����effortΪ��"+task.getCost());
			System.out.println("����"+task.getNumber()+"����Ҫ����"+task.getSkills().size());
			for(Skill ski:task.getSkills()){
				
				System.out.println("����"+task.getNumber()+"���輼��"+ski.getNumber()+"��ֵΪ��"+ski.getValue());
			}
			System.out.println("����"+task.getNumber()+"����Ҫְ��"+employeeNumber_Task[task.getNumber()]);
			for(Employee employee:task.getEmployees()){
				
				System.out.println("����"+task.getNumber()+"����Ա���ı��Ϊ"+employee.getNumber());
			}
			
			System.out.println("");
		}
		
		System.out.println("----------------------------------------------------------------------");
		
		
		
		
		System.out.println("----------------------------------------------------------------------");
		System.out.println("��ְ֤���Ƿ��Ѿ�ȫ������ȷ�洢��");
		for(Employee employee:employees){
			System.out.println("ְ��"+employee.getNumber()+"н��Ϊ��"+employee.getSalary());
			System.out.println("ְ��"+employee.getNumber()+"���߱�����"+employee.getSkills().size());
			for(Skill ski:employee.getSkills()){
				
				System.out.println("ְ��"+employee.getNumber()+"�߱�����"+ski.getNumber()+"��ֵΪ��"+ski.getValue());
			}
			System.out.println("");
		}
		
		
		System.out.println("----------------------------------------------------------------------");
		System.out.println("��֤���Ƿ��Ѿ�ȫ������ȷ�洢��");
		for(Arc arc:arcs){
			System.out.println("��"+arc.getNumber()+"���ɽ��"+arc.getFirstNumber()+"ָ��"+arc.getLastNumber());
		}
		
		
		System.out.println("Test is over!");
	
	}
}
