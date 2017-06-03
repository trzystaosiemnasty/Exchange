package pl.javageek.exchange.user;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@Controller
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @RequestMapping("/")
    public String displayExchange() {
        return "exchange";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String displaySignUp(WebRequest request, Model model) {
        val user = new User();
        user.getPortfolio().add(new PortfolioItem());
        model.addAttribute("user", user);
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signUp(@ModelAttribute("user") @Valid User user, BindingResult result) {
        filterIllegalCurrencies(user);
        removeDuplicates(user);
        mapNullAmountsToZero(user);
        if (!result.hasErrors()) {
            try {
                userService.registerNewUserAccount(user);
            } catch (UsernameExistsException e) {
                result.rejectValue("username", "message.regError");
            }
        }
        if (result.hasErrors()) {
            return "signup";
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping(value="/signup", params={"addRow"})
    public String addRow(final User user, final BindingResult bindingResult) {
        filterIllegalCurrencies(user);
        removeDuplicates(user);
        mapNullAmountsToZero(user);
        addNewRowIfZeroEmptyRows(user);
        return "signup";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String displayEdit(WebRequest request, Model model, Principal principal) {
        val user = userRepository.findByUsername(principal.getName()).orElseThrow(RuntimeException::new);
        model.addAttribute("user", user);
        return "edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String edit(@ModelAttribute("user") @Valid User user, BindingResult result) {
        filterIllegalCurrencies(user);
        removeDuplicates(user);
        mapNullAmountsToZero(user);
        if (!result.hasErrors()) {
            userService.updateUser(user);
        }
        if (result.hasErrors()) {
            return "edit";
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping(value="/edit", params={"addRowEdit"})
    public String addRowEdit(final User user, final BindingResult bindingResult) {
        filterIllegalCurrencies(user);
        removeDuplicates(user);
        mapNullAmountsToZero(user);
        addNewRowIfZeroEmptyRows(user);
        return "edit";
    }

    private void removeDuplicates(User user) {
        val map = new HashMap<String, Integer>();
        for (val p : user.getPortfolio()) {
            map.put(p.getKey(), p.getValue());
        }
        val list = map.entrySet().stream()
                .map(e -> new PortfolioItem(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        user.setPortfolio(list);
    }

    private void addNewRowIfZeroEmptyRows(User user) {
        val currencies = user.getPortfolio().stream()
                .map(PortfolioItem::getKey)
                .collect(Collectors.toList());
        if (!currencies.contains("")) {
            user.getPortfolio().add(new PortfolioItem());
        }
    }

    private void filterIllegalCurrencies(User user) {
        val allowedCurrencies = new HashSet<>(Arrays.asList("USD", "EUR", "CHF", "RUB", "CZK", "GBP"));
        user.setPortfolio(user.getPortfolio().stream()
                .filter(p -> allowedCurrencies.contains(p.getKey()))
                .collect(Collectors.toList()));
    }

    private void mapNullAmountsToZero(User user) {
        user.getPortfolio().stream().filter(p -> p.getValue() == null).forEach(p -> p.setValue(0));
    }
}
