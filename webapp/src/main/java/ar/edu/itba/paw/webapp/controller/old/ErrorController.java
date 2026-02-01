/*
package ar.edu.itba.paw.webapp.controller.old;

@Controller
public class ErrorController implements Redirect {
    // This extra redirect needs to be done or otherwise the page will load without the logged user
    @RequestMapping("/error/400")
    public ModelAndView badRequestRedirect() {
        return redirect("/error/badRequest");
    }

    @RequestMapping("/error/403")
    public ModelAndView forbiddenRedirect() {
        return redirect("/error/forbidden");
    }

    @RequestMapping("/error/404")
    public ModelAndView notFoundRedirect() {
        return redirect("/error/notFound");
    }

    @RequestMapping("/error/500")
    public ModelAndView internalServerErrorRedirect() {
        return redirect("/error/internalError");
    }

    @RequestMapping("/error/badRequest")
    public ModelAndView badRequest() {
        return new ModelAndView("error/400");
    }


    @RequestMapping("/error/forbidden")
    public ModelAndView forbidden() {
        return new ModelAndView("error/403");
    }

    @RequestMapping("/error/notFound")
    public ModelAndView notFound() {
        return new ModelAndView("error/404");
    }

    @RequestMapping("/error/internalError")
    public ModelAndView internalServerError() {
        return new ModelAndView("error/500");
    }
}*/
