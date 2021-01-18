

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.itextpdf.text.DocumentException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;

@WebServlet("/")
public class InventoryServelet extends HttpServlet {
    private static final long serialVersionUID = 1;
    private InventoryDAO invenDAO;

    public void init() {
        invenDAO = new InventoryDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();

        try {
            switch (action) {
                case "/new":
                    showNewForm(request, response);
                    break;
                case "/insert":
                	insertProduct(request, response);
                    break;
                case "/delete":
                	deleteProduct(request, response);
                    break;
                case "/edit":
                    showEditForm(request, response);
                    break;
                case "/update":
                	updateProduct(request, response);
                    break;
                case "/download":
                	downloadList(request, response);
                    break;
                default:
                	listedProduct(request, response);
                    break;
            }
        } catch (SQLException | DocumentException ex) {
            throw new ServletException(ex);
        }
    }
    
	private void listedProduct(HttpServletRequest request, HttpServletResponse response)
    	    throws SQLException, IOException, ServletException {
    	        List < Inventory > listProduct = invenDAO.selectAllPro();
    	        request.setAttribute("listProduct", listProduct);
    	        RequestDispatcher dispatcher = request.getRequestDispatcher("product_list.jsp");
    	        dispatcher.forward(request, response);
    	    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
        		RequestDispatcher dispatcher = request.getRequestDispatcher("product_form.jsp");
        		dispatcher.forward(request, response);
    }

    private void insertProduct(HttpServletRequest request, HttpServletResponse response)
    throws SQLException, IOException {
        String name = request.getParameter("name");
        String rat = request.getParameter("rating");
        int rating=Integer.parseInt(rat);
        
        Inventory newInvetory = new Inventory(name, rating);
        invenDAO.insertProduct(newInvetory);
        response.sendRedirect("list");
    }
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
    	    throws SQLException, ServletException, IOException {
    	        int id = Integer.parseInt(request.getParameter("id"));
    	        Inventory existingProduct = invenDAO.selectPro(id);
    	        RequestDispatcher dispatcher = request.getRequestDispatcher("product_form.jsp");
    	        request.setAttribute("product", existingProduct);
    	        dispatcher.forward(request, response);
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
    	    throws SQLException, IOException {
    	        int id = Integer.parseInt(request.getParameter("id"));
    	        String name = request.getParameter("name");
    	        int rating = Integer.parseInt(request.getParameter("rating"));

    	        Inventory upd_inven = new Inventory(id, name, rating);
    	        invenDAO.updateProduct(upd_inven);
    	        response.sendRedirect("list");
    	    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
    		throws SQLException, IOException {
            int id = Integer.parseInt(request.getParameter("id"));
            invenDAO.deleteProduct(id);
            response.sendRedirect("list");
    	    }
    private void downloadList(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException, DocumentException{
    	response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Product List.pdf";
        response.setHeader(headerKey, headerValue);

		List<Inventory> inventory = invenDAO.selectAllPro();
		DownloadList downloadobject = new DownloadList(inventory);
		downloadobject.downloadList(response);
    }
}
