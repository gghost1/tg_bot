package ru.tg_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.tg_bot.model.form.Form;
import ru.tg_bot.repository.FormRepository;

@Service
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;

    @Value("${bot.pageSize}")
    private int pageSize;

    public void create(String name, String email, int mark) {
        formRepository.save(new Form(null, name, email, mark));
    }

    public Page<Form> getAll(int iteration) {
        return formRepository.findAll(PageRequest.of(iteration, pageSize, Sort.by("id")));
    }

}
