package spittr.web;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import spittr.Spittle;
import spittr.SpittleForm;
import spittr.data.SpittleRepository;
import spittr.exception.DuplicateSpittleException;
import spittr.exception.SpittleNotFoundException;

@Controller
@RequestMapping("/spittles")
public class SpittleController {

	private static final String MAX_LONG_AS_STRING = "" + Long.MAX_VALUE;
	private SpittleRepository spittleRepository;

	@Autowired
	public SpittleController(SpittleRepository spittleRepository) {
		this.spittleRepository = spittleRepository;
	}

	/*
	 * Alternate, explicit implementation - Update the model with the relevant
	 * attributes and return the name of the view used to render the updated
	 * model
	 * 
	 * @RequestMapping(method = RequestMethod.GET) 
	 * public String spittles(
	 * 		@RequestParam(value = "max", defaultValue = MAX_LONG_AS_STRING) long max,
	 * 		@RequestParam(value = "count", defaultValue = "20") int count, Model model) {
	 * 		model.addAttribute(spittleRepository.findSpittles(max, count));
	 * 		return "spittles";
	 *  }
	 */

	/*
	 * Implicit implementaiton, view name will be inferred from Request Path
	 * (i.e. spittles) and retruned value will be added to model (model
	 * attribute name inferred from return type spittleList)
	 */
	@RequestMapping(method = RequestMethod.GET)
	public List<Spittle> spittles(@RequestParam(value = "max", defaultValue = MAX_LONG_AS_STRING) long max,
			@RequestParam(value = "count", defaultValue = "20") int count, Model model) {
		return spittleRepository.findSpittles(max, count);
	}

	@RequestMapping(value = "/{spittleId}", method = RequestMethod.GET)
	public String spittle(@PathVariable("spittleId") long spittleId, Model model) throws SpittleNotFoundException {
		Spittle spittle = spittleRepository.findOne(spittleId);
		if (spittle == null) {
			throw new SpittleNotFoundException();
		}
		model.addAttribute(spittleRepository.findOne(spittleId));
		return "spittle";
	}
	
	@RequestMapping(value = "/{spittleId}", method=RequestMethod.POST)
	public String saveSpittle(SpittleForm form, Model model) {
		spittleRepository.save(new Spittle(form.getMessage(), new Date(),
				form.getLongitude(), form.getLatitude()));
		return "redirect:/spittles";
	}
	

	@ExceptionHandler(DuplicateSpittleException.class)
	public String handleDuplicateSpittle() {
		return "error/duplicate";
	}
}
