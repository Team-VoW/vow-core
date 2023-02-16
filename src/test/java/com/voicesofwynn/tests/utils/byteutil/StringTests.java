package com.voicesofwynn.tests.utils.byteutil;

import com.voicesofwynn.TestSettings;
import com.voicesofwynn.core.utils.ByteUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class StringTests {

    @SuppressWarnings({"ResultOfMethodCallIgnored", "resource"})
    @Test
    void readWriteTest() throws IOException {
        String[] testInts = new String[] {
                "Hello",
                "Hello 2.0",
                "Random Strings GO!",
                "\n some special symbols test \t \0",
                "\0 more null tests",
                "1111111",
                "Some more testing",
                "Long string test - Long string test - Long string test - Long string test |> - <| |> - <| |> - <| more symbols because yeah, Loremipsumdolorsitamet,consecteturadipiscingelit.Fuscequammetus,aliqueteumalesuadaet,elementumvitaenulla.Nuncsedsemperpurus,atinciduntex.Proineutortorneceratconsequateuismodidamauris.Duisrhoncusestpharetrafaucibusvarius.Quisqueimperdietloremutconsequatposuere.Proinconvallisaugueacurnaconguelaoreet.Praesentinlobortisenim.Maurisegetvulputateodio.Quisqueidfermentumipsum,euconsecteturturpis.Nullautloremnecliguladictumiaculisegetegetmauris.Pellentesqueviverravelvelitidullamcorper.Classaptenttacitisociosquadlitoratorquentperconubianostra,perinceptoshimenaeos.Integervolutpatnislaligulaconsecteturefficitur.Etiamanullaex.Sedegetultricesnisi.Donecmetusante,pellentesqueafermentumsitamet,tinciduntsitametdolor.Duisacpurustempor,ornareantepretium,variusjusto.Fusceelementumleoaullamcorperultrices.Praesentnecnibhmassa.Nullametvelitvelerosvehiculaelementumetutvelit.Vestibulumsitamettellusquismagnaegestasmollissediddolor.Suspendissemaximushendreriturna,nonmaximuseratvenenatiset.Suspendissepotenti.Nullamsuscipitnonexasuscipit.Praesentplaceratduisedconsecteturelementum.Praesentnonliberobibendumnisiporttitortempus.Fuscevitaemaximusmi,idvestibulumlectus.Donecidtemporvelit.Proindictum,turpisvelelementumtincidunt,eratmassafaucibuspurus,utconvallisleonuncetest.Vivamusimperdietanteacduieleifendvenenatis.Proineunibhligula.Vestibulummalesuadaeleifendexegetfermentum.Seduterossedaugueconsecteturcursusvelvitaeurna.Sedactinciduntdolor.Maecenasimperdietantevelsollicitudinfeugiat.Donecsedlacussollicitudin,euismodenimquis,commodomauris.Curabiturdapibussedmaurisidtempor.Nulladuimagna,mollisasollicitudinsed,blanditaante.Crasconsecteturviverralacinia.Crasfaucibuslectusconguejustolobortis,sedfermentumerosvestibulum.Pellentesquehendreritenimetnisiauctor,acconguequamsuscipit.Pellentesquevelnibhleo.Proinhendreritfelissollicitudin,pharetraexsed,mollislacus.Pellentesquetemporipsumnonliberomattis,idpretiumnisiefficitur.Aliquameratvolutpat.Maurissedfringillaarcu",
                "Special symbols also ˚ßå∑œ¡™£˜˜∂ƒßµƒ∂ßµ˚Ω≈å😂😌☠️🥻⛎🚦",
                "time for some actual wynn text tests",
                "[1/2] Caravan Driver: Agh!",
                "[5/16] ???: Aah, Nasea!",
                "[8/16] Lari: But, there was hardly an effect. Your idea was, unfortunately, a dead end.",
                "[1/5] Ope: Hello! Young traveler.",
                "[1/4] Jenprest: Soldier! Good timing. We've been requesting help for ages.",
                "[1/1] Seaskipper Captain: Where are ya headed? Nemract, huh? A'ight! I'll get ya there in no time.",
                "[1/1] Seaskipper Captain: Where are ya headed? Rooster Island, huh? A'ight! I'll get ya there in no time."
        };

        File f = new File(TestSettings.TEST_DIR, "byteUtils/strTests");
        f.getParentFile().mkdirs();
        if (f.exists())
            Files.delete(f.toPath());
        Files.createFile(f.toPath());
        FileOutputStream stream = new FileOutputStream(f);

        for (String s : testInts) {
            System.out.println(s);
            stream.write(ByteUtils.encodeString(s));
        }
        stream.close();
        FileInputStream inp = new FileInputStream(f);

        for (String s : testInts) {
            assert ByteUtils.readString(inp).equals(s);
        }
        inp.close();
    }
}
