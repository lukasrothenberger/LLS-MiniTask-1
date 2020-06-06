package main;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jgraph.JGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTImporter;

import Graph.GraphWrapper;
import Graph.InvertableEdge;
import Graph.Node;
import guru.nidi.graphviz.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.*;
import guru.nidi.graphviz.model.Factory.*;
import guru.nidi.graphviz.parse.Parser;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxConstants;
import com.mxgraph.model.*;

import org.slf4j.*;

import com.mxgraph.canvas.mxBasicCanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;

import org.jgrapht.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.*;
import org.jgrapht.ext.*;
import org.jgrapht.traverse.*;

public class Main {

	public static void main(String[] args) throws Exception {
		GraphWrapper graph = new GraphWrapper();
		
		// DEBUG (equivalent to aig_0_min.aag)
		graph.addInputNode(2);
		graph.addInputNode(4);
		graph.addInputNode(6);
		graph.addInputNode(8);
		graph.addInputNode(10);
		graph.addInputNode(12);

		graph.addOutputNode(40);
		
		graph.addAndGate(14, 13, 6);
		graph.addAndGate(16, 12, 9);
		graph.addAndGate(18, 16, 7);
		graph.addAndGate(20, 19, 15);
		graph.addAndGate(22, 21, 11);
		graph.addAndGate(24, 17, 6);
		graph.addAndGate(26, 13, 8);
		graph.addAndGate(28, 11, 7);
		graph.addAndGate(30, 28, 27);
		graph.addAndGate(32, 25, 19);
		graph.addAndGate(34, 32, 31);
		graph.addAndGate(36, 35, 23);
		graph.addAndGate(38, 5, 3);
		graph.addAndGate(40, 38, 37);
		
		System.out.println("###### DOT FORMAT - can be pasted into DOT viewer ######");
	    System.out.println(graph.toDOTFormat());
		
		System.out.println("##### BLIF FORMAT #####");
		System.out.println(graph.toBLIFFormat());
		
		System.out.println("#### Export Graph to DOT Format and create PNG image. ####");
		graph.printToDOTandPNG("unmodifiedGraph");

	}
	
}
