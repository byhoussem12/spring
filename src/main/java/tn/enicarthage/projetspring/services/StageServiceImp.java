package tn.enicarthage.projetspring.services;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tn.enicarthage.projetspring.entities.Stage;
import tn.enicarthage.projetspring.entities.StageETE;
import tn.enicarthage.projetspring.entities.StagePFE;
import tn.enicarthage.projetspring.repositories.StageETERepository;
import tn.enicarthage.projetspring.repositories.StagePFERepository;
import tn.enicarthage.projetspring.repositories.StageRepository;
@Service
@CrossOrigin(origins = "http://localhost:4200/home") 
public class StageServiceImp implements IStageService {
	@Autowired
	private StageRepository stagerepo ;
	@Autowired
	private StagePFERepository PFErepo ;
	@Autowired
	private StageETERepository ETErepo ;
	public List<Stage> getall() {
            return stagerepo.findAll();
	}
	public String scraperHiinterns() {
        int startPage = 1;
        int endPage = 30;
        String urlPattern = "https://hi-interns.com/internships?page=%d";
        List<Stage> allStageData = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (int pageNumber = startPage; pageNumber <= endPage; pageNumber++) {
            String currentUrl = String.format(urlPattern, pageNumber);
            try {
                Document doc = Jsoup.connect(currentUrl).get();
                Elements stageCards = doc.select("section.relative.overflow-clip");

                for (Element stageCard : stageCards) {
                    Element stageTitleElement = stageCard.select("p.text-xs.text-gray-500").first();
                    String stageTitle =stageCard.select("a.mr-11.line-clamp-2.text-base.font-bold.text-gray-800").text().trim();
                    String stageSociete = stageCard.select("button > div.flex.items-start > div > div > h2 > p").text().trim();
                    String description = stageCard.select(" p.mb-2.text-sm.font-medium.text-gray-700").text().trim();
                    String img = stageCard.select("img").attr("src").trim();
                    String expiration =(stageTitleElement!=null)? stageTitleElement.text().trim() : "";
                    String duree = stageCard.select("div.bg-purple-100.text-purple-800").text().trim();
                    String necessarity =stageCard.select("div.flex.flex-wrap.gap-1 > div.bg-gray-100").text().trim();
                    if((description!=""|stageTitle!=""|stageSociete!="")&duree.contains("6")) {
                    	StagePFE stage = new StagePFE();
                        stage.setTitre(stageTitle);
                        stage.setNom(stageSociete);
                        stage.setDescription(description);
                        stage.setDuree(duree);
                        stage.setImg(img);
                        stage.setNecessarity(necessarity);
                        stage.setExpiration(expiration);
                    	allStageData.add(stage); 
                    	Optional<Stage> existant = stagerepo.findByDescriptionAndTitreAndNom(description, stageTitle, stageSociete);
                    	if (!existant.isPresent()) {
                    	  stagerepo.save(stage);
                    	  
                    	}
                    
                    }
                    else if((description!=""|stageTitle!=""|stageSociete!="")&!duree.contains("6")) {
                    	StageETE stage = new StageETE();
                        stage.setTitre(stageTitle);
                        stage.setNom(stageSociete);
                        stage.setDescription(description);
                        stage.setDuree(duree);
                        stage.setImg(img);
                        stage.setExpiration(expiration);
                    	allStageData.add(stage); 
                    	Optional<Stage> existant = stagerepo.findByDescriptionAndTitreAndNom(description, stageTitle, stageSociete);
                    	if (!existant.isPresent()) {
                    	  stagerepo.save(stage);
                    	  
                    	}
                    }
                     
                }
                
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            return mapper.writeValueAsString(allStageData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Erreur lors de la conversion des données en JSON.";
        }
    }
	public List<StagePFE> getallPFE() {
        return PFErepo.findAll();
}
	public List<StageETE> getallETE() {
        return ETErepo.findAll();
}
	public List<StageETE> getallE() {
        return ETErepo.findAll();
}
}