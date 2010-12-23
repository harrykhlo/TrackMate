package fiji.plugin.trackmate.visualization.trackscheme;

import ij.ImagePlus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.ext.JGraphModelAdapter.CellFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;

import fiji.plugin.trackmate.Feature;
import fiji.plugin.trackmate.Settings;
import fiji.plugin.trackmate.Spot;

public class TrackSchemeFrame extends JFrame {

	/*
	 * CONSTANTS
	 */
	

	public static final int Y_COLUMN_SIZE = 100;
	public static final int X_COLUMN_SIZE = 100;
	
	private static final long serialVersionUID = 1L;
	private static final Dimension DEFAULT_SIZE = new Dimension( 530, 320 );
	private static final Color BACKGROUND_COLOR_1 = Color.GRAY;
	private static final Color BACKGROUND_COLOR_2 = Color.LIGHT_GRAY;
	private static final Color LINE_COLOR = Color.BLACK;
	
	/*
	 * INNER CLASSES
	 */
	
	private static class GraphPane extends JPanel {

		private static final long serialVersionUID = 1L;
		private TreeSet<Float> instants;

		public GraphPane(SimpleGraph<Spot, DefaultEdge> graph) {
			super();
			setBackground(BACKGROUND_COLOR_1);
			
			instants = new TreeSet<Float>();
			for (Spot s : graph.vertexSet())
				instants.add(s.getFeature(Feature.POSITION_T));
				
		}
		
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int width = getSize().width;
			int height = getSize().height;
			
			// Alternating row color
			g.setColor(BACKGROUND_COLOR_2);
			int y = 0;
			while (y < height) {
				g.fillRect(0, y, width, Y_COLUMN_SIZE);
				y += 2*Y_COLUMN_SIZE;
			}
			
			// Header separator
			g.setColor(LINE_COLOR);
			g.drawLine(0, Y_COLUMN_SIZE, width, Y_COLUMN_SIZE);
			g.drawLine(X_COLUMN_SIZE, 0, X_COLUMN_SIZE, height);
			
			// Row headers
			int x = X_COLUMN_SIZE / 4;
			y = 3 * Y_COLUMN_SIZE / 2;
			for(Float instant : instants) {
				g.drawString("t="+instant, x, y);
				y += Y_COLUMN_SIZE;
			}
			
			// Column headers
			
		}
		
	}
	

	/*
	 * FIELDS
	 */
	
	
	private SimpleGraph<Spot, DefaultEdge> trackGraph;
	private Settings settings;

	/*
	 * CONSTRUCTORS
	 */
	
	public TrackSchemeFrame(SimpleGraph<Spot, DefaultEdge> trackGraph, Settings settings) {
		this.trackGraph = trackGraph;
		this.settings = settings;
		init();
	}
	
	
	/*
	 * METHODS
	 */
	
	
	private void init() {
		float[] calibration = new float[] { settings.dx, settings.dy, settings.dz };
		float radius = settings.segmenterSettings.expectedRadius;
		ImagePlus imp = settings.imp;
		
		JGraphModelAdapter<Spot, DefaultEdge> jgAdapter = new JGraphModelAdapter<Spot, DefaultEdge>(
				trackGraph,
				JGraphModelAdapter.createDefaultVertexAttributes(), 
				JGraphModelAdapter.createDefaultEdgeAttributes(trackGraph),
				new CellFactory<Spot, DefaultEdge>() {

					@Override
					public org.jgraph.graph.DefaultEdge createEdgeCell(DefaultEdge e) {
						return new org.jgraph.graph.DefaultEdge();
					}

					@Override
					public DefaultGraphCell createVertexCell(Spot s) {
						return new SpotCell(s);
					}
				});
		
		
		SpotCellViewFactory factory = new SpotCellViewFactory(imp, radius, calibration);
		
		GraphLayoutCache graphLayoutCache = new GraphLayoutCache(jgAdapter, factory);
		JGraph jgraph = new JGraph(jgAdapter, graphLayoutCache);
		
		GraphPane backPane = new GraphPane(trackGraph);
		BorderLayout layout = new BorderLayout();
		backPane.setLayout(layout);
		backPane.add(jgraph, BorderLayout.CENTER);
		jgraph.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(backPane);
		getContentPane().add(scrollPane);
        setSize( DEFAULT_SIZE );
        
        JGraphFacade facade = new JGraphFacade(jgraph);
        JGraphLayout graphLayout = new JGraphTimeLayout(trackGraph, jgAdapter);
        
        graphLayout.run(facade);
        Map nested = facade.createNestedMap(false, false); // Obtain a map of the resulting attribute changes from the facade 
        jgraph.getGraphLayoutCache().edit(nested); // Apply the results to the actual graph 
        
	}
}