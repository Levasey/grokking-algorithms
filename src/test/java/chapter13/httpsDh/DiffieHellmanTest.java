package chapter13.httpsDh;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiffieHellmanTest {

    @Test
    void textbookExample_sharedSecretMatches() {
        DiffieHellman.Parameters p = DiffieHellman.textbookDemoParameters();
        DiffieHellman.Party alice = new DiffieHellman.Party(p, BigInteger.valueOf(6));
        DiffieHellman.Party bob = new DiffieHellman.Party(p, BigInteger.valueOf(15));

        assertEquals(BigInteger.valueOf(8), alice.publicKey());
        assertEquals(BigInteger.valueOf(19), bob.publicKey());
        assertEquals(BigInteger.TWO, alice.sharedSecret(bob.publicKey()));
        assertEquals(BigInteger.TWO, bob.sharedSecret(alice.publicKey()));
    }

    @Test
    void randomParties_agreeOnPreMaster() {
        DiffieHellman.Parameters params = DiffieHellman.textbookDemoParameters();
        SecureRandom rng = new SecureRandom();
        for (int i = 0; i < 30; i++) {
            DiffieHellman.Party a = new DiffieHellman.Party(params, rng);
            DiffieHellman.Party b = new DiffieHellman.Party(params, rng);
            assertEquals(a.sharedSecret(b.publicKey()), b.sharedSecret(a.publicKey()));
        }
    }

    @Test
    void httpsHandshake_derivesIdenticalDirectionalKeysFromEitherSide() {
        DiffieHellman.Parameters params = DiffieHellman.textbookDemoParameters();
        SecureRandom rng = new SecureRandom();
        DiffieHellman.Party server = new DiffieHellman.Party(params, rng);
        DiffieHellman.Party client = new DiffieHellman.Party(params, rng);

        BigInteger pmClient = client.sharedSecret(server.publicKey());
        BigInteger pmServer = server.sharedSecret(client.publicKey());
        assertEquals(pmClient, pmServer);

        HttpsKeyAgreement.SessionKeys fromClient = HttpsKeyAgreement.deriveSessionKeys(pmClient);
        HttpsKeyAgreement.SessionKeys fromServer = HttpsKeyAgreement.deriveSessionKeys(pmServer);

        assertArrayEquals(fromClient.clientToServerKey(), fromServer.clientToServerKey());
        assertArrayEquals(fromClient.serverToClientKey(), fromServer.serverToClientKey());
    }

    @Test
    void performDheHandshake_producesUsableKeys() {
        HttpsKeyAgreement.SessionKeys keys = HttpsKeyAgreement.performDheHandshake(new SecureRandom());
        assertEquals(32, keys.clientToServerKey().length);
        assertEquals(32, keys.serverToClientKey().length);
    }

    @Test
    void parametersRejectInvalidModulus() {
        assertThrows(IllegalArgumentException.class,
                () -> new DiffieHellman.Parameters(BigInteger.TWO, BigInteger.valueOf(1)));
    }
}
